# kie_sandbox

![Version: 0.0.0](https://img.shields.io/badge/Version-0.0.0-informational?style=flat-square) ![Type: application](https://img.shields.io/badge/Type-application-informational?style=flat-square) ![AppVersion: 0.0.0](https://img.shields.io/badge/AppVersion-0.0.0-informational?style=flat-square)

A Helm chart to deploy KIE Sandbox on Kubernetes

## Values

| Key              | Type   | Default                                                                                                                                                                 | Description                                                                                                                               |
| ---------------- | ------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| autoscaling      | object | `{"enabled":false,"maxReplicas":100,"minReplicas":1,"targetCPUUtilizationPercentage":80}`                                                                               | KIE Sandbox HorizontalPodAutoscaler configuration (https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale/)            |
| env              | list   | `[{"name":"KIE_SANDBOX_EXTENDED_SERVICES_URL","value":"http://127.0.0.1:21345\""},{"name":"KIE_SANDBOX_CORS_PROXY_URL","value":"http://127.0.0.1:8081"}]`               | Env variables for KIE Sandbox deployment                                                                                                  |
| fullnameOverride | string | `""`                                                                                                                                                                    | Overrides charts full name                                                                                                                |
| image            | object | `{"account":"kie-tools","name":"kie-sandbox-image","pullPolicy":"IfNotPresent","registry":"quay.io","tag":"latest"}`                                                    | Image source configuration for the KIE Sandbox image                                                                                      |
| imagePullSecrets | list   | `[]`                                                                                                                                                                    | Pull secrets used when pulling KIE Sandbox image                                                                                          |
| ingress          | object | `{"annotations":{},"className":"","enabled":false,"hosts":[{"host":"kie-sandbox.domain.com","paths":[{"path":"/","pathType":"ImplementationSpecific"}]}],"tls":[]}`     | KIE Sandbox Ingress configuration (https://kubernetes.io/docs/concepts/services-networking/ingress/)                                      |
| name             | string | `"kie-sandbox"`                                                                                                                                                         | The KIE Sandbox application name                                                                                                          |
| nameOverride     | string | `""`                                                                                                                                                                    | Overrides charts name                                                                                                                     |
| openshiftRoute   | object | `{"annotations":{},"enabled":false,"host":"kie-sandbox.{{ .Values.global.openshiftRouteDomain }}","tls":{"insecureEdgeTerminationPolicy":"None","termination":"edge"}}` | KIE Sandbox OpenShift Route configuration (https://docs.openshift.com/container-platform/4.14/networking/routes/route-configuration.html) |
| service          | object | `{"nodePort":"","port":8080,"type":"ClusterIP"}`                                                                                                                        | KIE Sandbox Service configuration (https://kubernetes.io/docs/concepts/services-networking/service/)                                      |
| serviceAccount   | object | `{"annotations":{},"create":true,"name":""}`                                                                                                                            | KIE Sandbox ServiceAccount configuration (https://kubernetes.io/docs/concepts/security/service-accounts/)                                 |

---

Autogenerated from chart metadata using [helm-docs v1.11.3](https://github.com/norwoodj/helm-docs/releases/v1.11.3)
