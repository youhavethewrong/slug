/* slug.groovy
 * An Internet scraper.
 * Requires HTTPBuilder, which has a few deps.
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

def findAtUrl(url, regex) {
    println "Checking $url for $regex"
    def http = new HTTPBuilder( url )
     
    http.request(GET,TEXT) { req ->
        headers.'User-Agent' = 'Mozilla/5.0'

        response.success = { resp, reader ->
            def s = regex.matcher(reader.text)
            return s.findAll() as Set
        }

        response.'404' = { resp ->
            println 'Not found'
            return []
        }
    }
}

def slug() {
    if (args.size() < 1) {
        usage()
        System.exit(1)
    }

    def url = args[0]

    linksRegex = ~/<a\s+href=["']((?:https?:\/\/)?.*?)["']/
    paraRegex = ~/<p>(.*?)<\/p>/

    findAtUrl(url, linksRegex).each { m ->
        println "Searching: "+url+m[1]
        findAtUrl(url+m[1], paraRegex).each { n ->
            println n
        }
    }
}

slug()
