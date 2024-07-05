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

package common

import (
	"context"
	"fmt"
	"io"
	"os"
	"path/filepath"

	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/metadata"
	v1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/util/yaml"
	"k8s.io/client-go/kubernetes"
)

type Document struct {
	Kind     string `yaml:"kind"`
	Metadata struct {
		Name string `yaml:"name"`
	} `yaml:"metadata"`
}

func CheckOperatorInstalled() error {
	config, err := KubeRestConfig()
	if err != nil {
		return fmt.Errorf("❌ ERROR: Unable to get Kubernetes config %w", err)
	}

	clientset, err := kubernetes.NewForConfig(config)
	if err != nil {
		return fmt.Errorf("❌ ERROR: Unable to create Kubernetes client %w", err)
	}

	pods, err := clientset.CoreV1().Pods(metadata.OperatorName).List(context.TODO(), metav1.ListOptions{})
	if err != nil {
		return fmt.Errorf("❌ ERROR: Unable to get pods %w", err)
	}

	operatorRunning := checkOperatorRunning(pods)
	if !operatorRunning {
		return fmt.Errorf("❌ ERROR: SonataFlow Operator not found")
	}

	fmt.Println(" - ✅ SonataFlow Operator is available")
	return nil
}

func checkOperatorRunning(pods *v1.PodList) bool {
	for _, pod := range pods.Items {
		if pod.Status.Phase == v1.PodRunning {
			return true
		}
	}
	return false
}

func FindServiceFiles(directory string) ([]string, error) {
	var serviceFiles []string

	err := filepath.Walk(directory, func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return fmt.Errorf("❌ ERROR: failure accessing a path %q: %v\n", path, err)
		}

		if info.IsDir() || filepath.Ext(path) != ".yaml" {
			return nil
		}

		file, err := os.Open(path)
		if err != nil {
			return fmt.Errorf("❌ ERROR: failure opening file %q: %v\n", path, err)
		}
		defer file.Close()

		byteValue, err := io.ReadAll(file)
		if err != nil {
			return fmt.Errorf("❌ ERROR: failure reading file %q: %v\n", path, err)
		}

		var doc Document
		if err := yaml.Unmarshal(byteValue, &doc); err != nil {
			return fmt.Errorf("❌ ERROR: failure unmarshalling YAML from file %q: %v\n", path, err)
		}

		if doc.Kind == metadata.ManifestServiceFilesKind {
			serviceFiles = append(serviceFiles, path)
		}

		return nil
	})

	if err != nil {
		return nil, err
	}

	return serviceFiles, nil
}
