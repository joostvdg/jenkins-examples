# LDAP

## OpenLDAP

Very well maintained and used image.


### Kubernetes

```bash
kubectl exec -ti ldap-0 ldapadd -x -D "cn=admin,dc=kearos,dc=net" -w admin -f /container/service/slapd/assets/test/new-user.ldif -H ldap://ldap.example.org -ZZ
```

```bash
kubectl exec -it ldap-0 -- /bin/bash
```

## OpenDJ

Deprecated, but easier to use.

### Jenkins Config

```
Server  : opendj4:389
root DN : dc=example,dc=com
User Search Base : ou=People
Group search base : (empty)
Group Membership type : search ldap
Group Membership search : (empty)
Manager DN : uid=idm, ou=Administrators,dc=example,dc=com
Manager Password : *****
Display name : cn
Mail : mail
```