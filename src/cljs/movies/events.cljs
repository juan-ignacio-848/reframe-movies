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

(defn the-movie-database-uri [path api-key]
  (let [path (add-starting-slash path)]
    (str "https://api.themoviedb.org/3" path "?api_key=" api-key)))

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




(re-frame/reg-event-db
  ::initialize-db
  (fn-traced [_ _]
             db/default-db))

(re-frame/reg-event-db
 ::set-active-panel
 (fn-traced [db [_ active-panel]]
   (assoc db :active-panel active-panel)))
