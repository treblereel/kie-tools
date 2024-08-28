package k8sclient

import (
	"context"
	"fmt"
	clientsetfake "k8s.io/apiextensions-apiserver/pkg/client/clientset/clientset/fake"
	"k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/apis/meta/v1/unstructured"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/client-go/dynamic"
	"k8s.io/client-go/dynamic/fake"
	"k8s.io/client-go/kubernetes/scheme"
	"os"
)

type Fake struct{}

func (m Fake) GetNamespace() (string, error) {
	return "default", nil
}

func (m Fake) CheckContext() (string, error) {
	return "default", nil
}

func (m Fake) ExecuteApply(path, namespace string) error {
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

	fakeDynamic, err := getFakeDynamicClient()
	if err != nil {
		return fmt.Errorf("❌ ERROR: Failed to create dynamic fake Kubernetes client: %v", err)
	}

	_, err = fakeDynamic.Resource(gvr).Namespace(namespace).Create(context.Background(), unstructuredMap, metav1.CreateOptions{})
	if err != nil {
		return fmt.Errorf("❌ ERROR: Failed to create resource: %w", err)
	}
	return nil
}

func (m Fake) ExecuteDelete(crd, namespace string) error {

	return nil
}

func (m Fake) CheckCrdExists(crd string) (bool, error) {
	fakeClientSet := clientsetfake.NewSimpleClientset()
	_, err := fakeClientSet.ApiextensionsV1().CustomResourceDefinitions().Get(context.Background(), crd, metav1.GetOptions{})
	if err != nil {
		return false, err
	}
	return true, nil
}

func getFakeDynamicClient() (dynamic.Interface, error) {
	scheme := runtime.NewScheme()
	fakeDynamicClient := fake.NewSimpleDynamicClient(scheme)
	return fakeDynamicClient, nil
}
