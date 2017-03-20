(ns attendify.parsers
  (:require [services.dribbble :as dribbble]))

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
    (->> @(dribbble/find-followers-by-user username)
         (map #(deref (dribbble/find-shots-by-user (:username (:follower %)))))
         (flatten)
         (map #(deref (dribbble/find-likes-by-shot (:id %))))
         (flatten)
         (map #(:username (:user %)))
         (frequencies)
         (print-results))))
