package k8sclient

import (
	"context"
	"fmt"
	"k8s.io/apiextensions-apiserver/pkg/client/clientset/clientset"
	"os"
	"path/filepath"
	"strings"

	"k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/apis/meta/v1/unstructured"
	"k8s.io/apimachinery/pkg/util/yaml"

	"k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/client-go/dynamic"
	"k8s.io/client-go/rest"
	"k8s.io/client-go/tools/clientcmd"
	"k8s.io/client-go/tools/clientcmd/api"
)

type GoAPI struct{}

func (m GoAPI) GetNamespace() (string, error) {
	fmt.Println("🔎 Checking current namespace in k8s...")

	config, err := KubeApiConfig()
	if err != nil {
		return "", fmt.Errorf("❌ ERROR: Failed to get current k8s namespace: %w", err)
	}
	namespace := config.Contexts[config.CurrentContext].Namespace

	if len(namespace) == 0 {
		namespace = "default"
	}
	fmt.Printf(" - ✅  k8s current namespace: %s\n", namespace)
	return namespace, nil
}

func (m GoAPI) CheckContext() (string, error) {
	config, err := KubeApiConfig()
	if err != nil {
		return "", fmt.Errorf("❌ ERROR: No current k8s context found %w", err)
	}
	context := config.CurrentContext
	if context == "" {
		return "", fmt.Errorf("❌ ERROR: No current k8s context found")
	}
	fmt.Printf(" - ✅ k8s current context: %s\n", context)
	return context, nil
}

func (m GoAPI) ExecuteApply(path, namespace string) error {
	client, err := DynamicClient()
	if err != nil {
		return fmt.Errorf("❌ ERROR: Failed to create dynamic Kubernetes client: %v", err)
	}
	fmt.Printf("🔨 Applying YAML file %s\n", path)

	if namespace == "" {
		currentNamespace, err := m.GetNamespace()
		if err != nil {
			return fmt.Errorf("❌ ERROR: Failed to get current namespace: %w", err)
		}
		namespace = currentNamespace
	}

	if resources, err := ParseYamlFile(path); err != nil {
		return fmt.Errorf("❌ ERROR: Failed to parse YAML file: %v", err)
	} else {
		created := make([]unstructured.Unstructured, 0, len(resources))
		for _, resource := range resources {
			err := DoApply(client, resource, namespace)
			if err != nil {
				// rollback
				for _, r := range created {
					gvk := resource.GroupVersionKind()
					gvr, _ := meta.UnsafeGuessKindToResource(gvk)

					if err := client.Resource(gvr).Namespace(namespace).Delete(context.Background(), r.GetName(), metav1.DeleteOptions{}); err != nil && !errors.IsNotFound(err) {
						fmt.Printf("❌ ERROR: Failed to deploy YAML file: %s", path)
						return fmt.Errorf("❌ ERROR: Failed to rollback resource: %v", err)
					}
				}
				return fmt.Errorf("❌ ERROR: Failed to create resource: %v", err)
			}
			created = append(created, resource)
		}
	}
	return nil
}

func (m GoAPI) ExecuteDelete(path, namespace string) error {
	client, err := DynamicClient()
	if err != nil {
		return fmt.Errorf("❌ ERROR: Failed to create dynamic Kubernetes client: %v", err)
	}

	if namespace == "" {
		currentNamespace, err := m.GetNamespace()
		if err != nil {
			return fmt.Errorf("❌ ERROR: Failed to get current namespace: %w", err)
		}
		namespace = currentNamespace
	}

	if resources, err := ParseYamlFile(path); err != nil {
		return fmt.Errorf("❌ ERROR: Failed to parse YAML file: %v", err)
	} else {
		deletePolicy := metav1.DeletePropagationForeground
		for _, resource := range resources {
			gvk := resource.GroupVersionKind()
			gvr, _ := meta.UnsafeGuessKindToResource(gvk)
			err = client.Resource(gvr).Namespace(namespace).Delete(context.Background(), resource.GetName(), metav1.DeleteOptions{
				PropagationPolicy: &deletePolicy,
			})
			if err != nil {
				return fmt.Errorf("❌ ERROR: Failed to delete Resource: %w", err)
			}
		}
	}
	return nil
}

func (m GoAPI) CheckCrdExists(crd string) (bool, error) {
	config, err := KubeRestConfig()
	if err != nil {
		return false, fmt.Errorf("❌ ERROR: Failed to create rest config for Kubernetes client: %v", err)
	}

	crdClientSet, err := clientset.NewForConfig(config)
	if err != nil {
		return false, fmt.Errorf("❌ ERROR: Failed to create CRD client: %v", err)
	}

	crdList, err := crdClientSet.ApiextensionsV1().CustomResourceDefinitions().Get(context.Background(), crd, metav1.GetOptions{})
	if err != nil {
		return false, fmt.Errorf("❌ ERROR: Failed to list CRDs: %v", err)
	}
	if crdList == nil {
		return false, nil
	}

	return true, nil
}

func (m GoAPI) CheckResourceExists(resource unstructured.Unstructured, namespace string) (bool, error) {
	client, err := DynamicClient()
	if err != nil {
		return false, fmt.Errorf("❌ ERROR: Failed to create dynamic Kubernetes client: %v", err)
	}
	gvk := resource.GroupVersionKind()
	gvr, _ := meta.UnsafeGuessKindToResource(gvk)

	_, err = client.Resource(gvr).Namespace(namespace).Get(context.Background(), resource.GetName(), metav1.GetOptions{})
	if err != nil {
		if errors.IsNotFound(err) {
			return false, nil
		}
		return false, fmt.Errorf("❌ ERROR: Failed to get resource: %v", err)
	}
	return true, nil
}

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

var DynamicClient = func() (dynamic.Interface, error) {
	config, err := KubeRestConfig()
	if err != nil {
		return nil, fmt.Errorf("❌ ERROR: Failed to create rest config for Kubernetes client: %v", err)
	}

	dynamicClient, err := dynamic.NewForConfig(config)
	if err != nil {
		return nil, fmt.Errorf("❌ ERROR: Failed to create dynamic Kubernetes client: %v", err)
	}

	return dynamicClient, nil
}

var ParseYamlFile = func(path string) ([]unstructured.Unstructured, error) {
	data, err := os.ReadFile(path)
	if err != nil {
		return nil, fmt.Errorf("❌ ERROR: Failed to read YAML file: %w", err)
	}
	decoder := yaml.NewYAMLOrJSONDecoder(strings.NewReader(string(data)), 4096)
	result := []unstructured.Unstructured{}
	for {
		rawObj := &unstructured.Unstructured{}
		err := decoder.Decode(rawObj)
		if err != nil {
			break
		}
		result = append(result, *rawObj)
	}
	return result, nil
}

var DoApply = func(client dynamic.Interface, resource unstructured.Unstructured, namespace string) error {
	gvk := resource.GroupVersionKind()
	gvr, _ := meta.UnsafeGuessKindToResource(gvk)
	_, err := client.Resource(gvr).Namespace(namespace).Create(context.Background(), &resource, metav1.CreateOptions{})
	return err
}
