# Using OpenShift Service Mesh to encrypt Broker communication

## Install the AMQ Operator

See doc

## Install The OpenShift Service Mesh Operator

See doc

## Deploy the application

```
oc apply -k openshift/base
```

## Inject the sidecars

```
oc label namespace <mynamespace> istio-injection=enabled
```

## Patch the amq statfulset
```
oc patch statefulset/artemis-ss -p '{"spec":{"template":{"metadata":{"annotations":{"sidecar.istio.io/inject":"true", "traffic.sidecar.istio.io/excludeInboundPorts":"7800"}}}}}'
```