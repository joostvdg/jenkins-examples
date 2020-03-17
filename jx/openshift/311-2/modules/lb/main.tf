# https://access.redhat.com/documentation/en-us/reference_architectures/2018/html/deploying_and_managing_openshift_3.9_on_google_cloud_platform/red_hat_openshift_container_platform_prerequisites#environment_configuration
# Health check
# $ gcloud compute health-checks create https ${CLUSTERID}-master-lb-healthcheck \
#     --port 443 --request-path "/healthz" --check-interval=10s --timeout=10s \
#     --healthy-threshold=3 --unhealthy-threshold=3

resource "google_compute_health_check" "master" {
    name         = "https"
    healthy_threshold = 3
    unhealthy_threshold = 3
    timeout_sec        = 10
    check_interval_sec = 10
    https_health_check {
        request_path = "/healthz"
        port = "443"
    }
}

resource "google_compute_http_health_check" "nodes" {
    name         = "nodes"
    request_path = "/healthz"
    port = 1936
    healthy_threshold = 3
    unhealthy_threshold = 3
    timeout_sec        = 10
    check_interval_sec = 10
}

# Create backend and set client ip affinity to avoid websocket timeout
# $ gcloud compute backend-services create ${CLUSTERID}-master-lb-backend \
#     --global \
#     --protocol TCP \
#     --session-affinity CLIENT_IP \
#     --health-checks ${CLUSTERID}-master-lb-healthcheck \
#     --port-name ocp-api

resource "google_compute_backend_service" "master" {
    name          = "backend-service-master"
    health_checks = [ "${google_compute_health_check.master.self_link}" ]
    # load_balancing_scheme  = "EXTERNAL"
    protocol = "TCP"
    session_affinity =  "CLIENT_IP"
    port_name   = "ocp-api"
    backend {
        group = var.instance_group_master
    }
}

resource "google_compute_target_tcp_proxy" "master_tcp_proxy" {
    name            = "master-tcp-proxy"
    backend_service = google_compute_backend_service.master.self_link
}

resource "google_compute_global_forwarding_rule" "master_forwarding" {
    name                  = "master-forwarding"
    ip_address         = var.master_ip
    target                = google_compute_target_tcp_proxy.master_tcp_proxy.self_link
    port_range            = 443
}


# Target Pool
# $ gcloud compute target-pools create ${CLUSTERID}-infra \
#     --http-health-check ${CLUSTERID}-infra-lb-healthcheck

# $ for i in $(seq 0 $(($INFRA_NODE_COUNT-1))); do
#   gcloud compute target-pools add-instances ${CLUSTERID}-infra \
#   --instances=${CLUSTERID}-infra-${i}
# done
resource "google_compute_target_pool" "nodes" {
    name = "nodes"

    instances = var.instance_group_nodes

    health_checks = [
        google_compute_http_health_check.nodes.name
    ]
}

resource "google_compute_forwarding_rule" "network-load-balancer-http" {
    name                  = "network-load-balancer-http"
    ip_address         = var.nodes_ip
    target                  =   google_compute_target_pool.nodes.self_link
    port_range            = "80"
    ip_protocol           = "TCP"
}

resource "google_compute_forwarding_rule" "network-load-balancer-https" {
    name                  = "network-load-balancer-https"
    ip_address         = var.nodes_ip
    target                =     google_compute_target_pool.nodes.self_link
    port_range            = "443"
    ip_protocol           = "TCP"
}

# # Forwarding rules and firewall rules
# $ export APPSLBIP=$(gcloud compute addresses list \
#   --filter="name:${CLUSTERID}-apps-lb" --format="value(address)")

# $ gcloud compute forwarding-rules create ${CLUSTERID}-infra-http \
#     --ports 80 \
#     --address ${APPSLBIP} \
#     --region ${REGION} \
#     --target-pool ${CLUSTERID}-infra

# $ gcloud compute forwarding-rules create ${CLUSTERID}-infra-https \
#     --ports 443 \
#     --address ${APPSLBIP} \
#     --region ${REGION} \
#     --target-pool ${CLUSTERID}-infra