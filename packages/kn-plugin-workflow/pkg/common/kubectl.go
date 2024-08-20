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
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common/k8sclient"
)

type K8sApi interface {
	GetKubectlNamespace() (string, error)
	CheckKubectlContext() (string, error)
	ExecuteKubectlApply(crd, namespace string) error
	ExecuteKubectlDelete(crd, namespace string) error
	CheckKubectlCrdExists(crd string) (bool, error)
}

var Current K8sApi = k8sclient.GoAPI{}

func CheckKubectlContext() (string, error) {
	return Current.GetKubectlNamespace()
}

func GetKubectlNamespace() (string, error) {
	return Current.GetKubectlNamespace()
}

func ExecuteKubectlApply(crd, namespace string) error {
	return Current.ExecuteKubectlApply(crd, namespace)
}

func ExecuteKubectlDelete(crd, namespace string) error {
	return Current.ExecuteKubectlDelete(crd, namespace)
}

func CheckKubectlCrdExists(crd string) (bool, error) {
	return Current.CheckKubectlCrdExists(crd)
}
