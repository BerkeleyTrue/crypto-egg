(ns axios.core
  (:refer-clojure :exclude [get])
  (:require
    ["axios" :as axiosjs]
    [taoensso.timbre :as log]
    [utils]))


(defn- config->js [conf]
  (let [base-url (:base-url conf)
        ^js/Object js-map (utils/cljkk->js conf)]
    (when base-url
      (set! (. js-map -baseURL) base-url))
    js-map))

(comment (config->js {:base-url "/foo"}))

(defn- map-js->cljskk [p]
  (.then p utils/js->cljkk))

(def ^:private invoke-and-map-respose (comp map-js->cljskk js-invoke))

(defn
  axios
  "Requests can be made by passing the relevant config to axios.
  Returns a promise.

  // Send a POST request
  (axios {:method \"post\"
          :url \"/user/12345\"
          :data
          {:firstName \"Fred\",
           :lastName \"Flintstone\"}});

  // Send a GET request (default method)
  (axios \"/user/12345\");

  "
  ([url-or-config]
   (map-js->cljskk
     (if (string? url-or-config)
       (axiosjs url-or-config)
       (axiosjs (config->js url-or-config)))))
  ([url config] (map-js->cljskk (axiosjs url (config->js config)))))

(defn create-client
  "You can create a new client of axios with a custom config.

  (def client
    (create-client axios {:base-url \"https://some-domain.com/api/\"
                          :timeout 1000
                          :headers {\"X-Custom-Header\" \"foobar\"})
  "
  [config]
  (.create axiosjs (config->js config)))


(defn request [client config]
  (invoke-and-map-respose client "request" config))

(defn get
  ([url] (get axiosjs url))
  ([client url] (invoke-and-map-respose client "get" url))
  ([client url config] (invoke-and-map-respose client "get" url (config->js config))))

(defn delete
  ([url] (delete axiosjs url))
  ([client url] (invoke-and-map-respose client "delete" url))
  ([client url config] (invoke-and-map-respose client "delete" url (config->js config))))

(defn head
  ([url] (head axiosjs url))
  ([client url] (invoke-and-map-respose client "head" url))
  ([client url config] (invoke-and-map-respose client "head" url (config->js config))))

(defn options
  ([url] (options axiosjs url))
  ([client url] (invoke-and-map-respose client "options" url))
  ([client url config] (invoke-and-map-respose client "options" url (config->js config))))

(defn post
  ([url] (post axiosjs url))
  ([client url] (invoke-and-map-respose client "post" url))
  ([client url config] (invoke-and-map-respose client "post" url (config->js config))))

(defn put
  ([url] (put axiosjs url))
  ([client url] (invoke-and-map-respose client "put" url))
  ([client url config] (invoke-and-map-respose client "put" url (config->js config))))

(defn patch
  ([url] (patch axiosjs url))
  ([client url] (invoke-and-map-respose client "patch" url))
  ([client url config] (invoke-and-map-respose client "patch" url (config->js config))))

(defn get-uri
  ([url] (get-uri axiosjs url))
  ([client url] (invoke-and-map-respose client "getUri" url))
  ([client url config] (invoke-and-map-respose client "getUri" url (config->js config))))
