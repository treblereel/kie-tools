/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package quarkus

import (
	"context"
	"fmt"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common"
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common/k8sclient"
	"github.com/spf13/afero"
	"golang.org/x/exp/slices"
	"k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/apis/meta/v1/unstructured"
	"k8s.io/client-go/dynamic"
	"os"
	"os/exec"
	"path/filepath"
	"strconv"
	"testing"
)

type testDeploy struct {
	input          DeployCmdConfig
	expected       bool
	createFile     string
	yaml           string
	resourcesCount int
}

const defaultPath = "./target/kubernetes"

var testRunDeploy = []testDeploy{
	{input: DeployCmdConfig{Path: defaultPath}, expected: true, createFile: "kogito.yml", yaml: "knative.yml", resourcesCount: 2},
	{input: DeployCmdConfig{Path: "./different/folders"}, expected: true, createFile: "kogito.yml", yaml: "knative.yml", resourcesCount: 2},
	{input: DeployCmdConfig{Path: "different/folders"}, expected: true, createFile: "kogito.yml", yaml: "knative.yml", resourcesCount: 2},
	{input: DeployCmdConfig{Path: "different/folders"}, expected: true, createFile: "kogito.yml", yaml: "complex-knative.yml", resourcesCount: 8},
	{input: DeployCmdConfig{Path: "different/folders"}, expected: true, createFile: "kogito.yml", yaml: "complex-knative2.yml", resourcesCount: 5},
	{input: DeployCmdConfig{Path: "different/folders"}, expected: true, createFile: "kogito.yml", yaml: "01-sonataflow_hello.yaml", resourcesCount: 1},
	{input: DeployCmdConfig{Path: "different/folders"}, expected: true, createFile: "kogito.yml", yaml: "sonataflow-complex.yaml", resourcesCount: 4},
	{input: DeployCmdConfig{}, expected: false, createFile: "test"},
	{input: DeployCmdConfig{}, expected: false},
}

func fakeRunDeploy(testIndex int) func(command string, args ...string) *exec.Cmd {
	return func(command string, args ...string) *exec.Cmd {
		cs := []string{"-test.run=TestHelperRunDeploy", "--", command}
		cs = append(cs, args...)
		cmd := exec.Command(os.Args[0], cs...)
		cmd.Env = []string{fmt.Sprintf("GO_TEST_HELPER_RUN_DEPLOY_IMAGE=%d", testIndex)}
		return cmd
	}
}

func TestHelperRunDeploy(t *testing.T) {
	testIndex, err := strconv.Atoi(os.Getenv("GO_TEST_HELPER_RUN_DEPLOY_IMAGE"))
	if err != nil {
		return
	}
	out := []string{"Test", strconv.Itoa(testIndex)}
	if testRunDeploy[testIndex].createFile != "" {
		out = append(out, "with creating", testRunDeploy[testIndex].createFile, "file")
	}
	fmt.Fprintf(os.Stdout, "%v", out)
	os.Exit(0)
}

func TestRunDeploy(t *testing.T) {
	common.FS = afero.NewMemMapFs()
	originalParseYamlFile := k8sclient.ParseYamlFile
	originalDynamicClient := k8sclient.DynamicClient
	orginalApply := k8sclient.DoApply

	fakeClient := k8sclient.Fake{FS: common.FS}

	defer func() {
		k8sclient.ParseYamlFile = originalParseYamlFile
		k8sclient.DynamicClient = originalDynamicClient
		k8sclient.DoApply = orginalApply
	}()

	k8sclient.ParseYamlFile = fakeClient.FakeParseYamlFile
	k8sclient.DynamicClient = fakeClient.FakeDynamicClient

	for _, test := range testRunDeploy {
		expectedResources := []string{}
		createdResources := []string{}
		if test.createFile != "" {
			if test.input.Path == "" {
				test.input.Path = defaultPath
			}
			common.CreateFolderStructure(t, test.input.Path)
			common.CreateFileInFolderStructure(t, test.input.Path, test.createFile)

			if test.yaml != "" {
				common.CopyKnativeYaml(t, test.input.Path, test.yaml)
				if temp, err := k8sclient.ParseYamlFile(filepath.Join(test.input.Path, "knative.yml")); err != nil {
					t.Errorf("Expected nil error, got %#v", err)
				} else {
					if len(temp) != test.resourcesCount {
						t.Errorf("Expected resources count: %v, actual %v", test.resourcesCount, len(temp))
					}
					for _, r := range temp {
						expectedResources = append(expectedResources, r.GetName())
					}
				}
			}
		}
		k8sclient.DoApply = func(client dynamic.Interface, resource unstructured.Unstructured, namespace string) error {
			createdResources = append(createdResources, resource.GetName())
			gvk := resource.GroupVersionKind()
			gvr, _ := meta.UnsafeGuessKindToResource(gvk)
			_, err := client.Resource(gvr).Namespace(namespace).Create(context.Background(), &resource, metav1.CreateOptions{})
			return err
		}

		out, err := deployKnativeServiceAndEventingBindings(test.input)
		if err != nil && test.expected {
			t.Errorf("Expected nil error, got %#v", err)
		}

		if out != test.expected {
			t.Errorf("Expected %v, got %v", test.expected, out)
		}

		if test.yaml != "" && !slices.Equal(expectedResources, createdResources) {
			t.Errorf("Expected resources count: %v, actual %v", len(expectedResources), len(createdResources))
		}

		if test.createFile != "" {
			common.DeleteFolderStructure(t, test.input.Path)
		}
	}
}
