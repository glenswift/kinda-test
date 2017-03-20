(ns services.dribble
  (:require [clj-http.client :as http]
            [taoensso.timbre :as timbre :refer [info warn error]]))

(def access-token "a2d0a840b763d848208e847aba24dc4a847645a6da5c54d516c5630c65edaa83")

(defn fetch
  "Fetches data from dribbble API by given `endpoint`"
  ([endpoint]
    (fetch endpoint 1))
  ([endpoint page]
    (let [url (str "https://api.dribbble.com/v1" endpoint)]
      (future
        (Thread/sleep 3000)
        (:body (http/get url {:query-params {"page" page "per_page" 100 "access_token" access-token}
                              :accept :json
                              :as :json
                              :insecure? true}))))))

(defn fetch-list
  "Fetches data from dribbble API by given `endpoint` to the end of pagination"
  ([endpoint]
    (fetch-list endpoint '() 1))
  ([endpoint data page]
   (info (str "--- page " page))
    (let [new-data @(fetch endpoint page)]
      (if (or (empty? new-data) (not= (mod (count new-data) 100) 0))
        (future (concat data new-data))
        (recur endpoint (concat data new-data) (inc page))))))

(defn find-followers-by-user
  "Fetches shots by given username"
  [username]
  (info "Fetching followers of user" username)
  (fetch-list (str "/users/" username "/followers")))

(defn find-shots-by-user
  "Fetches shots by given username"
  [username]
  (info "Fetching shots of user" username)
  (fetch-list (str "/users/" username "/shots")))

(defn find-likes-by-shot
  "Fetches likes by given shot"
  [shot]
  (info "Fetching likes of shot" shot)
  (fetch-list (str "/shots/" shot "/likes")))
