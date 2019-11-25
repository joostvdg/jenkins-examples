{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "cloudbees-core.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "cloudbees-core.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Return instance and name labels.
*/}}
{{- define "cloudbees-core.instance-name" -}}
app.kubernetes.io/instance: {{ .Release.Name | quote }}
app.kubernetes.io/name: {{ include "cloudbees-core.name" . | quote }}
{{- end -}}

{{/*
Return labels, including instance and name.
*/}}
{{- define "cloudbees-core.labels" -}}
{{ include "cloudbees-core.instance-name" . }}
app.kubernetes.io/managed-by: {{ .Release.Service | quote }}
helm.sh/chart: {{ include "cloudbees-core.chart" . | quote }}
{{- end -}}

{{- define "os.label" -}}
{{- if (semverCompare ">=1.14-0" .Capabilities.KubeVersion.GitVersion) }}kubernetes.io/os{{- else -}}beta.kubernetes.io/os{{- end -}}
{{- end -}}

{{- define "oc.protocol" -}}
{{- if .Values.OperationsCenter.Ingress.tls.Enable -}}https{{- else -}}{{ .Values.OperationsCenter.Protocol }}{{- end -}}
{{- end -}}

{{- define "oc.url" -}}
{{- template "oc.protocol" . -}}://{{ .Values.OperationsCenter.HostName }}{{ .Values.OperationsCenter.ContextPath }}
{{- end -}}

{{- define "ingress.ssl_redirect" -}}
{{- .Values.OperationsCenter.Ingress.tls.Enable }}
{{- end -}}

{{- define "rbac.apiVersion" -}}
{{- default .Values.rbac.apiVersion "rbac.authorization.k8s.io/v1" -}}
{{- end -}}

{{- define "rbac.apiGroup" -}}
{{- default .Values.rbac.apiGroup "rbac.authorization.k8s.io" -}}
{{- end -}}

{{- define "validate.operationscenter" -}}
{{- if and (.Values.OperationsCenter.Enabled) (.Values.Master.OperationsCenterNamespace) -}}
{{ fail "Can't use both OperationsCenter.Enabled=true and Master.OperationsCenterNamespace" }}
{{- end -}}
{{- end -}}