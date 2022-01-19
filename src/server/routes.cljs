(ns server.routes
  (:require
    [integrant.core :as ig]
    [reitit.ring :as ring]
    [macchiato.util.response :as r]
    [macchiato.middleware.params :as params]
    [macchiato.middleware.restful-format :as rf]
    [server.middlewares.logging :refer [wrap-with-logger]]))


(defn ping [_ res _]
  (-> {:message "ok"}
      (r/ok)
      (r/json)
      (res)))

(defn not-found []
  (-> {:message "404"}
      (r/not-found)
      (r/json)))

(defn lookup-price [req res]
  (let [coin (-> req :parameters :query :coin)]
    (-> {:message (str "Coin: " coin)}
        (r/ok)
        (r/json)
        (res))))

(def routes
  ["" {:no-doc true}
   ["/ping" {:get ping}]
   ["/api"
    ["/coin"
     ["/:coin" {:get {:handler lookup-price}}]]]])

(def app
  (ring/ring-handler
    (ring/router
      routes
      {:data {:middleware
              [params/wrap-params
               #(rf/wrap-restful-format % {:keywordize? true})]}})
    (ring/create-default-handler {:not-found not-found})))

(comment
  (not-found)
  (app {:request-method :get :uri "/rainbows"})
  (app {:request-method :get :uri "/api/coin/btc"}))

(defmethod ig/init-key :router/handler [] (wrap-with-logger app))
