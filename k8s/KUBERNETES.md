`kubectl create -f postgis-root-password-secret.yml`

`kubectl create -f postgis-pv.yml`

`kubectl create -f postgis-pvc.yml`

`kubectl create -f postgis-deployment.yml`

`kubectl create -f reactiveposts-config.yml`

`kubectl create -f pod-reader-role.yml`

`kubectl create -f reactiveposts-serviceaccount.yml`

`kubectl create -f reactiveposts-rolebinding.yml`

`kubectl apply -f reactiveposts-deployment.yml`
