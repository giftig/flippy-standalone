# Flippy (standalone)

## Purpose

This is a small project for serving the [giftig/flippy](https://www.github.com/giftig/flippy/)
API via a basic spray HTTP service. It's recommended to combine it with the admin interface
provided by flippy by importing the /static directory from that project and serving it with
nginx or another web server. See the
[giftig/flippy-tester](https://www.github.com/giftig/flippy-tester) project for an idea of how
to do that; it comes with a ready-to-go docker-compose environment using nginx to serve the
admin site.

## Using flippy

You can simply grab the jar and run it, specifying the interface to run on, the backend you
want, and where to find the backend, or you can grab the docker image, which is the jar ready
to go in a java-8 environment.

    docker pull giftig/flippy:latest
    docker run giftig/flippy:latest \
      --interface 0.0.0.0 \
      --port 80 --backend \
      redis --backend-host \
      redis --backend-port 6379

If desired you can also reconfigure the logging; the project uses logback.
