(ns attendify.core
  (:require [attendify.parsers :as parsers]
            [attendify.url-utils :as url-utils]
            [services.dribbble :as dribbble]))

(defn main
  "CLI entry point"
  [task & params]
  (if (= task "dribbble")
    (parsers/fetch-top-likers params))
  (if (= task "url")
    (url-utils/print-demo)))
