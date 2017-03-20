(ns attendify.core
  (:require [attendify.parsers :as parsers]
            [attendify.url-utils :as url-utils]
            [services.dribble :as dribble]))

(defn main
  "CLI entry point"
  [task & params]
  (if (= task "dribble")
    (parsers/fetch-top-likers params))
  (if (= task "url")
    (url-utils/print-demo)))
