(ns chalk.macro
  (:require
    [clojure.string :as string]
    [camel-snake-kebab.core :as csc :include-macros true]))


(defn- safe-case [case-f]
 (fn [x]
   (cond-> (subs (name x) 1)
     true (string/replace "_" "*")
     true case-f
     true (string/replace "*" "_")
     true (->> (str (first (name x))))
     (keyword? x) keyword)))

(def ^:private camel-case (safe-case csc/->camelCase))

(defmacro gen-methods [chalkjs & methods]
  (list* 'do
         (map
           (fn [method#]
             (let [fn-name# (symbol (name method#))
                   prop-name# (camel-case (str (name method#)))
                   fn-doc# (str "terminal string styling: " fn-name#)]
               `(defn ~fn-name# ~fn-doc# [#^String ~'string]
                  (~'js-invoke ~chalkjs ~prop-name# ~'string))))
           methods)))
