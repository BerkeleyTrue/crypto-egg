(ns server.infra.datascript
  (:require
    [integrant.core :as ig]
    [datascript.core :as d]))

(defmethod ig/init-key :db/conn [_ {:keys [schema]}]
  (d/create-conn schema))
