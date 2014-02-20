module.exports = (connect, options) ->
        express = require 'express'
        #livereloadUtilities = require 'grunt-contrib-livereload/lib/utils'
        app = express()

        app.configure ->
                #app.use livereloadUtilities.livereloadSnippet
                app.use express.logger 'dev'
                app.use express.bodyParser()
                app.use express.methodOverride()
                app.use express.errorHandler()
                app.use express.static options.base
                app.use app.router
                routes = require './server'
                routes app, options

                timeout = null

                (require 'fs').watch './server/server.coffee', { persistent: false }, (event, filename) ->
                        clearTimeout timeout if timeout
                        timeout = setTimeout ->
                                console.log "server.coffee file updated... reloading server"
                                for method of app.routes
                                        app.routes[method] = [] # clean the routes
                                delete require.cache[require.resolve './server'] # remove the server module from cache
                                routes = require './server' # reload the module
                                routes app, options # rerun the module
                                clearTimeout timeout if timeout
                        ,10

        connect(app).stack