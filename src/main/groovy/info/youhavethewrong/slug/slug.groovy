/* slug.groovy
 * An Internet scraper.
 *
 * ESC 2013.05.22
 */

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.GET
import static groovyx.net.http.ContentType.TEXT

def usage() {
    println "Usage: slug.groovy URL"
    println "reads response from the url"
}

def slug() {
    if (args.size() < 1) {
        usage()
        System.exit(1)
    }

    def url = args[0]
    def http = new HTTPBuilder( url )
     
    http.request(GET,TEXT) { req ->
        headers.'User-Agent' = 'Mozilla/5.0'

        response.success = { resp, reader ->
            def pat = ~/<a\s+href=(["']http.*?["'])/
            def s = pat.matcher(reader.text)
            s.findAll().each { m ->
                println m[1]
            }
        }

        response.'404' = { resp ->
            println 'Not found'
        }
    }
}

slug()
