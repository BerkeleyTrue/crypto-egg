(ns server.app.routes
  (:require
    [integrant.core :as ig]
    [reitit.ring :as ring]
    
    [macchiato.util.response :as r]
    [macchiato.middleware.defaults :as m.defaults]
    
    [com.wsscode.pathom3.interface.eql :as p.eql]
    
    [server.infra.middlewares.logging :refer [wrap-with-logger]]
    [server.infra.middlewares.restful :refer [wrap-restful-format]]))


(defn ping [_ res _]
  (-> {:message "ok"}
      (r/ok)
      (r/json)
      (res)))

(defn not-found []
  (-> {:message "404"}
      (r/not-found)
      (r/json)))

(defn create-pathom-handler [pathom-env]
  (let [pathom (p.eql/boundary-interface pathom-env)]
    (fn [{:keys [body]} res]
      (->
        (pathom body)
        (r/ok)
        (r/content-type "application/transit+json")
        (res)))))

(defn create-routes [{:keys [pathom-env]}]
  ["" {:no-doc true}
   ["/ping" {:get ping}]
   ["/api" (create-pathom-handler pathom-env)]])


(defmethod ig/init-key :app.routes/handler [_ {:keys [pathom-env]}]
  (ring/ring-handler
    (ring/router
      (create-routes {:pathom-env pathom-env})
      {:data {:middleware
              [wrap-with-logger
               #(m.defaults/wrap-defaults % m.defaults/api-defaults)
               wrap-restful-format]}})
    (ring/create-default-handler {:not-found not-found})))
