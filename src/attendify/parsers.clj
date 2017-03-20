(ns attendify.parsers
  (:require [services.dribble :as dribble]))

(defn print-results
  "Takes top likers from `results` and prints"
  [results]
  (clojure.pprint/pprint
    (take 10
          (into
            (sorted-map-by
              (fn [key1 key2]
                (compare (get results key2) (get results key1))))
            results))))


(defn fetch-top-likers
  "It does exactly what first part of the task wants"
  [params]
  (let [[username] params]
    (->> @(dribble/find-followers-by-user username)
         (map #(deref (dribble/find-shots-by-user (:username (:follower %)))))
         (flatten)
         (map #(deref (dribble/find-likes-by-shot (:id %))))
         (flatten)
         (map #(:username (:user %)))
         (frequencies)
         (print-results))))
