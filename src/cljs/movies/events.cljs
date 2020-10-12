(ns movies.events
  (:require
   [re-frame.core :as re-frame]
   [movies.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   day8.re-frame.http-fx
   [ajax.core :as ajax]))

;; The movie database config
(defonce api-key "73ea0a3b607641181f92c20d1f86099b")

(defn add-starting-slash [path]
  (if (clojure.string/starts-with? path "/") path (str "/" path)))

(defn query-params-str [query-params]
  (clojure.string/join "&" (map (fn [[k v]] (str (name k) "=" v)) query-params)))

(defn the-movie-database-uri
  ([path api-key]
   (the-movie-database-uri path api-key {}))
  ([path api-key query-params]
   (let [path         (add-starting-slash path)
         query-params (assoc query-params :api_key api-key)
         query-params (query-params-str query-params)]
     (str "https://api.themoviedb.org/3" path "?" query-params))))

(re-frame/reg-event-fx
  ::load-images-config
  (fn [{:keys [db]} _]
    {:http-xhrio {:method          :get
                  :uri             (the-movie-database-uri "/configuration" api-key)
                  :timeout         3000
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [:load-images-config-ok]
                  :on-failure      [:load-images-config-error]}}))

(re-frame/reg-event-db
  :load-images-config-ok
  (fn [db [_ result]]
    (assoc db :images-config (:images result))))

;; TODO: Error handling
(re-frame/reg-event-db
  :load-images-config-error
  (fn [db [_ result]]
    (assoc db :images-config-error result)))

;; SEARCH MOVIES
(re-frame/reg-event-fx
  ::search-movies
  (fn [{:keys [db]} [_ term]]
    {:http-xhrio {:method          :get
                  :uri             (the-movie-database-uri "/search/movie" api-key {:query term})
                  :timeout         3000
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [:search-result-ok term]
                  :on-failure      [:load-images-config-error]}})) ;; TODO: Error handling

(defn ->movie [movie]
  {:id           (:id movie)
   :vote-average (:vote_average movie)
   :title        (:title movie)
   :poster-path  (:poster_path movie)})

(defn ->search-result [result]
  (let [movies (map ->movie (:results result))]
    {:page          (:page result)
     :total-results (:total_results result)
     :total-pages   (:total_pages result)
     :search-term   (:term result)
     :movies        movies}))

(re-frame/reg-event-db
  :search-result-ok
  (fn [db [_ term result]]
    (assoc db :search-result (->search-result (assoc result :term term)))))

(re-frame/reg-event-db
  ::add-to-favorites
  (fn-traced [db [_ movie-id]]
             (update db :favorites (fnil conj #{}) movie-id)))
;; search movies

(re-frame/reg-event-db
  ::initialize-db
  (fn-traced [_ _]
             db/default-db))

(re-frame/reg-event-db
 ::set-active-panel
 (fn-traced [db [_ active-panel]]
   (assoc db :active-panel active-panel)))
