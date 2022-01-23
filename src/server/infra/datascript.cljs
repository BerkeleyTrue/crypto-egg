(ns server.infra.datascript
  (:require
    [integrant.core :as ig]
    [datascript.core :as d]))

(defmethod ig/init-key :infra.datascript/conn [_ {:keys [schema]}]
  (d/create-conn schema))
