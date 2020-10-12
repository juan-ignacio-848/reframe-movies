(ns movies.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
  ::name
  (fn [db]
    (:name db)))

(re-frame/reg-sub
  ::active-panel
  (fn [db _]
    (:active-panel db)))

(re-frame/reg-sub
  ::search-results
  (fn [db _]
    (let [base-url (get-in db [:images-config :base_url])
          size     "original"
          movies   (get-in db [:search-result :movies])]
      (mapv (fn [movie]
              (merge movie {:poster-path
                            (when-let [poster-path (:poster-path movie)] (str base-url size poster-path))})) movies))))

(re-frame/reg-sub
  ::favorited?
  (fn [db [_ movie-id]]
    (some #(= movie-id %) (:favorites db))))
