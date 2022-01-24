(ns server.infra.pathom
  (:require
    [com.wsscode.pathom3.connect.indexes :as pci]
    [com.wsscode.pathom.viz.ws-connector.core :as pvc]
    [com.wsscode.pathom.viz.ws-connector.pathom3 :as p.connector]
    [server.app.resolvers :as resolvers]
    [integrant.core :as ig]))


(def pathom-viz? false)

(defmethod ig/init-key :infra.pathom/env [_ {:keys [conn]}]
  (cond-> (pci/register {:datascript/conn conn} [resolvers/coin-price])
    pathom-viz?
    (p.connector/connect-env {::pvc/parser-id `pathom-env})))
