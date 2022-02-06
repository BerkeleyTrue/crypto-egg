(ns utils
  (:require
    [clojure.string :as string]
    [camel-snake-kebab.core :as csc :include-macros true]
    [camel-snake-kebab.extras :refer [transform-keys]]))


(defn safe-case [case-f]
  (fn [x]
    (cond-> (subs (name x) 1)
      true (string/replace "_" "*")
      true case-f
      true (string/replace "*" "_")
      true (->> (str (first (name x))))
      (keyword? x) keyword)))

(def camel-case (safe-case csc/->camelCase))
(def kebab-case (safe-case csc/->kebab-case))

(def
  ^{:arglist '([js-obj])
    :doc "Convert js object to cljs map with keyword keys."}
  js->cljk
  #(js->clj % :keywordize-keys true))


(def
  ^{:arglist '([js-obj])
    :doc "Convert js object to cljs map with camelCase string keys turned into kebab-case.
         (js->clj #js {'fooBar' 'baz'}) ;-> {:foo-bar 'baz'}"}
  js->cljkk
  (comp (partial transform-keys kebab-case) js->cljk))

(comment (js->cljkk #js {"fooBar" "baz"}))

(def
  ^{:arglist '([js-obj])
    :doc "Convert map to js object, camelCasing keys.
         (cljkk->js {:foo-bar 'baz'}) ;-> #js {'fooBar' 'baz'}"}
  cljkk->js
  (comp clj->js (partial transform-keys camel-case)))

(defn- keyword-fn [k]
  (if (namespace k)
    (string/replace (str k) ":" "")
    (name k)))

(comment
  (= (keyword-fn :foo) "foo")
  (= (keyword-fn ::foo) "utils/foo"))

(defn clj->js*
  "Convert clojure map to js object preserving namespaced keys.
  (clj->js* {::foo-baz \"baz\"}) ;-> {\"user/foo-baz\" \"baz\"}"
  [x]
  (clj->js x :keyword-fn keyword-fn))

(comment
  (clj->js* {:foo-bar "baz"
             ::foo-baz "baz"}))
