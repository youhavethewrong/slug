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
        headers.'User-Agent' = 'Slug 0.001'

        response.success = { resp, reader ->
            def s = regex.matcher(reader.text)
            return s.findAll() as Set
        }

        response.failure = { resp ->
            println "[ERROR] request failed with code: "+resp.status
            return []
        }
    }
}

def scrape(url, discoverRegex, contentRegex) {
    findAtUrl(url, discoverRegex).each { link ->
        nextUrl = link[1]
        paras = []
        if(nextUrl.startsWith("http")) {
            paras = findAtUrl(link[1], contentRegex)
        }
        else {
            paras = findAtUrl(url+link[1], contentRegex)
        }
        println paras
    }
}

def slug() {
    if (args.size() < 1) {
        usage()
        System.exit(1)
    }

    def url = args[0]

    linksRegex = ~/<a\s+href=["']((?:https?:\/\/)?[^(){}]*?)["']/
    paraRegex = ~/<p\s*.*?>(.*?)<\/p>/

    scrape(url, linksRegex, paraRegex)
}

slug()
