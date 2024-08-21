package k8sclient

type Fake struct{}

func (m Fake) GetNamespace() (string, error) {
	return "default", nil
}

func (m Fake) CheckContext() (string, error) {
	return "default", nil
}

func (m Fake) ExecuteApply(crd, namespace string) error {
	return nil
}

func (m Fake) ExecuteDelete(crd, namespace string) error {
	return nil
}

func (m Fake) CheckCrdExists(crd string) (bool, error) {
	return true, nil
}
