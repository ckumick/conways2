(defproject conways2 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :main ^:skip-aot conways2.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :user {:dependencies [[pjstadig/humane-test-output "0.7.0"]]
                    :injections [(require 'pjstadig.humane-test-output)
                     (pjstadig.humane-test-output/activate!)]}}
  :plugins [ [quickie "0.3.6"] ] )


