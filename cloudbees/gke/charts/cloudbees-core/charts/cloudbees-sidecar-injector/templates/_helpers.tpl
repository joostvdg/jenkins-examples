{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "sidecar-injector.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "sidecar-injector.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- $name := default .Chart.Name .Values.nameOverride -}}
{{- if contains $name .Release.Name -}}
{{- .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}
{{- end -}}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "sidecar-injector.chart" -}}
{{- .Chart.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Return instance and name labels.
*/}}
{{- define "sidecar-injector.instance-name" -}}
app.kubernetes.io/instance: {{ .Release.Name | quote }}
app.kubernetes.io/name: {{ include "sidecar-injector.name" . | quote }}
{{- end -}}

{{/*
Return labels, including instance and name.
*/}}
{{- define "sidecar-injector-common.labels" -}}
{{ include "sidecar-injector.instance-name" . }}
app.kubernetes.io/managed-by: {{ .Release.Service | quote }}
helm.sh/chart: {{ include "sidecar-injector.chart" . | quote }}
{{- end -}}

{{- define "sidecar-injector.labels" -}}
{{ include "sidecar-injector-common.labels" . }}
app.kubernetes.io/component: cloudbees-sidecar-injector
{{- end -}}

{{- define "sidecar-injector-init.labels" -}}
{{ include "sidecar-injector-common.labels" . }}
app.kubernetes.io/component: cloudbees-sidecar-injector-init
{{- end -}}

{{- define "os.label" -}}
{{- if (semverCompare ">=1.14-0" .Capabilities.KubeVersion.GitVersion) }}kubernetes.io/os{{- else -}}beta.kubernetes.io/os{{- end -}}
{{- end -}}