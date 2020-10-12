(ns movies.views
  (:require
   [re-frame.core :as re-frame]
   [re-com.core :as re-com]
   [reagent.core :as reagent]
   [movies.subs :as subs]

   [movies.events :as events]))

(defn search-bar [on-submit]
  (let [state        (reagent/atom "")
        on-submit-fn (fn [e] (.preventDefault e) (on-submit @state))]
    (fn []
      [:form {:on-submit on-submit-fn}
       [:div {:class "ui fluid icon input"}
        [:input {:type        "text"
                 :placeholder "Search..."
                 :on-change   #(reset! state (-> % .-target .-value))
                 :value       @state}]
        [:i {:class    "circular search link icon"
             :on-click on-submit-fn}]]])))

(defn card [movie]
  [:div {:class "ui card"}
   [:a {:class "image" :href "#"}
    [:img {:src (if-let [img (:poster-path movie)] img "/images/wireframe/image.png")}]]
   [:div {:class "content"}
    [:a {:class "header" :href "#"} (:title movie)]]
   [:div {:class "extra content"}
    [:span {:class "left floated thumbs up"}
     [:i {:class "thumbs up icon"}]
     (:vote-average movie)]
    [:span {:class "right floated star"}
     [:i {:class    (if @(re-frame/subscribe [::subs/favorited? (:id movie)]) "yellow star icon" "star icon")
          :on-click #(re-frame/dispatch [::events/add-to-favorites (:id movie)])}]]]])

(defn home-panel []
  [:div.ui.container
   [search-bar #(re-frame/dispatch [::events/search-movies %])]
   (let [movies @(re-frame/subscribe [::subs/search-results])]
     [:div {:class "ui grid"}
      (for [movie movies]
        [:div {:key (:id movie)}
         [card movie]])])])

(defn- panels [panel-name]
  (case panel-name
    :home-panel [home-panel]
    [:div]))

(defn show-panel [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    [re-com/v-box
     :height "100%"
     :children [[panels @active-panel]]]))
