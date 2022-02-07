(ns server.infra.datascript
  (:require
    [integrant.core :as ig]
    [datascript.core :as d]))

(defonce ^:private conn* (atom nil))

(defmethod ig/init-key :infra.datascript/conn [_ {:keys [schema]}]
  (if-let [conn @conn*]
    conn
    (let [conn (d/create-conn schema)]
      (reset! conn* conn)
      conn)))
