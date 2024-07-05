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
	"os"
	"path/filepath"

	"k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/apis/meta/v1/unstructured"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/apimachinery/pkg/util/yaml"

	"k8s.io/client-go/dynamic"
	"k8s.io/client-go/kubernetes/scheme"
	"k8s.io/client-go/rest"
	"k8s.io/client-go/tools/clientcmd"
	"k8s.io/client-go/tools/clientcmd/api"
)

func KubeApiConfig() (*api.Config, error) {
	homeDir, err := os.UserHomeDir()
	if err != nil {
		return nil, fmt.Errorf("error getting user home dir: %w", err)
	}
	kubeConfigPath := filepath.Join(homeDir, ".kube", "config")
	fmt.Printf("🔎 Using kubeconfig: %s\n", kubeConfigPath)
	config, err := clientcmd.LoadFromFile(kubeConfigPath)
	if err != nil {
		return nil, fmt.Errorf("❌ ERROR: Failed to load kubeconfig: %w", err)
	}
	return config, nil
}

func KubeRestConfig() (*rest.Config, error) {
	config, err := rest.InClusterConfig()
	if err != nil {
		homeDir, err := os.UserHomeDir()
		if err != nil {
			return nil, fmt.Errorf("error getting user home dir: %w", err)
		}
		kubeConfigPath := filepath.Join(homeDir, ".kube", "config")
		fmt.Printf("🔎 Using kubeconfig: %s\n", kubeConfigPath)
		config, err = clientcmd.BuildConfigFromFlags("", kubeConfigPath)
		if err != nil {
			return nil, fmt.Errorf("❌ ERROR: Failed to load kubeconfig: %w", err)
		}
	}
	return config, nil
}

func GetKubectlNamespace() (string, error) {
	fmt.Println("🔎 Checking current namespace in kubectl...")

	config, err := KubeApiConfig()
	if err != nil {
		return "", fmt.Errorf("❌ ERROR: Failed to get current kubectl namespace: %w", err)
	}
	namespace := config.Contexts[config.CurrentContext].Namespace

	if len(namespace) == 0 {
		namespace = "default"
	}
	fmt.Printf(" - ✅  kubectl current namespace: %s\n", namespace)
	return namespace, nil
}

func CheckKubectlContext() (string, error) {
	config, err := KubeApiConfig()
	if err != nil {
		return "", fmt.Errorf("❌ ERROR: No current kubectl context found %w", err)
	}
	context := config.CurrentContext
	if context == "" {
		return "", fmt.Errorf("❌ ERROR: No current kubectl context found")
	}
	fmt.Printf(" - ✅ kubectl current context: %s\n", context)
	return context, nil
}

func ExecuteKubectlApply(path, namespace string) error {
	config, err := KubeRestConfig()
	if err != nil {
		return fmt.Errorf("❌ ERROR: Failed to create rest config for Kubernetes client: %v", err)
	}

	client, err := dynamic.NewForConfig(config)
	if err != nil {
		return fmt.Errorf("❌ ERROR: Failed to create dynamic Kubernetes client: %v", err)
	}
	fmt.Printf("🔨 Applying YAML file %s\n", path)
	data, err := os.ReadFile(path)
	if err != nil {
		return fmt.Errorf("❌ ERROR: Failed to read YAML file: %w", err)
	}

	obj, _, err := scheme.Codecs.UniversalDeserializer().Decode(data, nil, nil)
	if err != nil {
		return fmt.Errorf("❌ ERROR: Failed to decode YAML: %w", err)
	}

	unstructuredObj, err := runtime.DefaultUnstructuredConverter.ToUnstructured(obj)
	if err != nil {
		return fmt.Errorf("❌ ERROR: Failed to decode YAML: %w", err)
	}

	unstructuredMap := &unstructured.Unstructured{Object: unstructuredObj}
	gvk := unstructuredMap.GroupVersionKind()
	gvr, _ := meta.UnsafeGuessKindToResource(gvk)

	var res dynamic.ResourceInterface
	if namespace == "" {
		res = client.Resource(gvr)
	} else {
		res = client.Resource(gvr).Namespace(namespace)
	}

	_, err = res.Create(context.TODO(), unstructuredMap, metav1.CreateOptions{})
	if err != nil {
		return fmt.Errorf("❌ ERROR: Failed to apply CRD: %w", err)
	}
	return nil
}

func ExecuteKubectlDelete(path, namespace string) error {
	config, err := KubeRestConfig()
	if err != nil {
		return fmt.Errorf("❌ ERROR: Failed to create rest config for Kubernetes client: %v", err)
	}

	dynamicClient, err := dynamic.NewForConfig(config)
	if err != nil {
		return fmt.Errorf("❌ ERROR: Failed to create dynamic Kubernetes client: %v", err)
	}

	fileData, err := os.ReadFile(path)
	if err != nil {
		return fmt.Errorf("❌ ERROR: Failed to read YAML file: %w", err)
	}

	obj := &unstructured.Unstructured{}
	err = yaml.Unmarshal(fileData, obj)
	if err != nil {
		return fmt.Errorf("❌ ERROR: Failed to unmarshal YAML: %w", err)
	}

	gvk := obj.GroupVersionKind()
	gvr, _ := meta.UnsafeGuessKindToResource(gvk)

	deletePolicy := metav1.DeletePropagationForeground
	var res dynamic.ResourceInterface

	if namespace == "" {
		res = dynamicClient.Resource(gvr)
	} else {
		res = dynamicClient.Resource(gvr).Namespace(namespace)
	}

	err = res.Delete(context.TODO(), obj.GetName(), metav1.DeleteOptions{
		PropagationPolicy: &deletePolicy,
	})
	if err != nil {
		return fmt.Errorf("❌ ERROR: Failed to delete Resource: %w", err)
	}
	return nil
}
