(ns attendify.url-utils
  (:require [clojure.string :as str]))

(defn or-empty-strings
  "Replaces nil-s with empty strings in given `list`"
  [list]
  (map #(if-not (nil? %) % "") list))

(defn split-config-string
  "Returns list of strings representing config of each part of url i.e. host(...)"
  [config-string]
  (map first (re-seq  #"(host|path|queryparam)\([\w.?=&/]+\);" config-string)))

(defn get-uri-from-config
  "Returns part of url from config"
  [config]
  (second (re-find (re-pattern "\\((.+)\\)") config)))

(defn replace-url-params-with-regexp
  "Replaces parameters in given `url` with regexp"
  [url]
  (str/replace url #"\?\w+" "([^?/]+)"))

(defn get-url-params-names
  "Returns names of parameters found in given `urls`"
  [urls]
  (->> (str/join ",separator," urls)
       (re-seq #"\?\w+")
       (map #(.substring % 1))
       (map keyword)))

(defn pattern
  "Creates pattern to compare with"
  [config-string]
  (let [configs (split-config-string config-string)
        [host path queryparam] (map get-uri-from-config configs)
        [host-re path-re queryparam-re] (map replace-url-params-with-regexp (or-empty-strings [host path queryparam]))
        param-names (get-url-params-names (or-empty-strings [host path queryparam]))]

    {:regexp (re-pattern (str "https{0,1}://"
                              host-re "/"
                              path-re
                              (if-not (empty? queryparam-re) "\\?" "")
                              queryparam-re
                              ))
     :params param-names}
    ))

(defn recognize
  "aha"
  [pattern url]
  (let [entries (rest (re-find (:regexp pattern) url))]
    (if (not-empty entries)
      (zipmap (:params pattern) entries)
      nil)))

(def twitter (pattern "host(twitter.com); path(?user/status/?id);"))
(def dribbble (pattern "host(dribbble.com); path(shots/?id); queryparam(offset=?offset);"))

(defn print-demo
  "Prints output of recognizing several patterns"
  []
  (println (recognize twitter "http://twitter.com/bradfitz/status/562360748727611392"))
  (println (recognize dribbble "https://dribbble.com/shots/1905065-Travel-Icons-pack?offset=1"))
  (println (recognize dribbble "https://twitter.com/shots/1905065-Travel-Icons-pack?offset=1")))
