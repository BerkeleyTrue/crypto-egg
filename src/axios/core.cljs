(ns axios.core
  (:refer-clojure :exclude [get])
  (:require
    ["axios" :as axiosjs]
    [axios.interceptor :as interceptor]
    [axios.utils :refer [config->js is-client?]]
    [utils]))


(def ^{:arglists '([client then-handler? catch-handler? opts?])
       :doc "Add request interceptor.
       (add-request-interceptor
         (fn then-handler [config]
           config)
         (fn catch-handler [err])
         {:run-when (fn [] true)
          :run-syncronous false})"}
     
     add-request-interceptor
     (interceptor/create-add-interceptor
       "request"
       {:wrap-then
        interceptor/wrap-request-interceptor}))

(def ^{:arglists '([client then-handler? catch-handler? opts?])
       :doc "Add response interceptor.
       (add-response-interceptor
         (fn then-handler [config]
           config)
         (fn catch-handler [err])
         {:run-when (fn [] true)
           :run-syncronous false})"}
     
     add-response-interceptor
     (interceptor/create-add-interceptor "response"))


;; ensure axios response is clj
;; may make this optional, on by default
(def response-js->clj-interceptor-id
  (add-response-interceptor axiosjs utils/js->cljkk))


(defn axios
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
   (if (string? url-or-config)
     (axiosjs url-or-config)
     (axiosjs (config->js url-or-config))))
  ([url config]
   {:pre [(string? url)]}
   (axiosjs url (config->js config))))

(defn create-client
  "You can create a new client of axios with a custom config.

  (def client
    (create-client
      {:base-url \"https://some-domain.com/api/\"
       :timeout 1000
       :headers {\"X-Custom-Header\" \"foobar\"}}
      {:cljify tre}))
  "
  ([config] (create-client axiosjs config {}))
  
  ([client-or-config config-or-opts]
   (if (is-client? client-or-config)
     (create-client client-or-config config-or-opts {})
     (create-client axiosjs client-or-config config-or-opts)))
  
  ([root-client config {:keys [cljify] :or {cljify true}}]
   (let [client (.create root-client (config->js config))]
     (when cljify
       (add-response-interceptor client utils/js->cljkk))
     client)))

(defn request [client config]
  (js-invoke client "request" config))

(defn get
  ([url] (get axiosjs url))
  ([client url] (js-invoke client "get" url))
  ([client url config] (js-invoke client "get" url (config->js config))))

(defn delete
  ([url] (delete axiosjs url))
  ([client url] (js-invoke client "delete" url))
  ([client url config] (js-invoke client "delete" url (config->js config))))

(defn head
  ([url] (head axiosjs url))
  ([client url] (js-invoke client "head" url))
  ([client url config] (js-invoke client "head" url (config->js config))))

(defn options
  ([url] (options axiosjs url))
  ([client url] (js-invoke client "options" url))
  ([client url config] (js-invoke client "options" url (config->js config))))

(defn- conj-client-first [args]
  (if (is-client? (first args))
    args
    (conj [axiosjs] args)))

(defn post
  "Create a POST request to a url.
  (post \"/api\" data? config?)
  (post client \"/api\" data? config?)"
  [& args]
  (let [[client url data config] (conj-client-first args)]
    (js-invoke client "post" url data (config->js config))))

(defn put
  "Create a PUT request to a url.
  (put \"/api\" data? config?)
  (put client \"/api\" data? config?)"
  [& args]
  (let [[client url data config] (conj-client-first args)]
    (js-invoke client "put" url data (config->js config))))

(defn patch
  "Create a PATCH request to a url.
  (patch \"/api\" data? config?)
  (patch client \"/api\" data? config?)"
  [& args]
  (let [[client url data config] (conj-client-first args)]
    (js-invoke client "patch" url data (config->js config))))

(defn get-uri
  ([client] (js-invoke client "getUri"))
  ([client config] (js-invoke client "getUri" (config->js config))))
