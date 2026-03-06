# docker tasks documentation

---

## concepts

### cmd vs entrypoint

| | cmd | entrypoint |
|---|---|---|
| purpose | default arguments, can be overridden | fixed executable, always runs |
| override | fully replaced by `docker run <args>` | arguments are appended, not replaced |
| use case | default values | the main process of the container |

**example:**
```dockerfile
entrypoint ["java", "-jar"]
cmd ["app.jar"]
```
running `docker run my-app other.jar` executes: `java -jar other.jar`

---

### copy vs add

| | copy | add |
|---|---|---|
| copies files | yes | yes |
| extracts archives | no | yes |
| downloads from url | no | yes |
| recommended | yes (default choice) | only when extra features needed |

---

## problem 1 — hello-world container

```bash
# run the container
docker run hello-world

# check container status (shows all including stopped)
docker ps -a

# start the stopped container
docker start <container_id>

# remove the container
docker rm <container_id>

# remove the image
docker rmi hello-world
```

**result:** container ran, printed success message, was stopped, restarted, then removed along with its image.

---

## problem 2 — ubuntu interactive container

```bash
# run ubuntu in interactive mode
docker run -it ubuntu

# inside the container — run echo command
echo docker

# open bash and create a file
bash
touch hello-docker

# exit and stop the container
exit
docker stop <container_id>

# remove all stopped containers
docker container prune
```

**comment on hello-docker file:** the file was lost after the container stopped. it was created inside the container filesystem with no volume mounted, so it did not persist. this is expected behavior — containers are stateless by default.

---

## problem 3 — postgres database container

```bash
docker run -d \
  --name app-database \
  -e postgres_password=p4ssw0rd0! \
  postgres
```

- `-d` — runs in background (detached)
- `--name` — names the container `app-database`
- `-e` — sets the environment variable for the root password

**verify it is running:**
```bash
docker ps
```

---

## problem 4 — nginx with static html

```bash
# run nginx container
docker run -d -p 8080:80 --name my-nginx nginx

# copy html file into the running container
docker cp index.html my-nginx:/usr/share/nginx/html/index.html

# commit the container as a new image
docker commit my-nginx my-nginx-image
```

**verify:** open `http://localhost:8080` in browser — custom html page is served.

**index.html used:**
```html
<!doctype html>
<html>
  <body>
    <h1>hello from docker!</h1>
  </body>
</html>
```

---

## problem 5 — python app containerized

### app.py
```python
print("hello from python container!")
```

### dockerfile
```dockerfile
from python:3.11-alpine
workdir /app
copy app.py .
cmd ["python", "app.py"]
```

```bash
# build the image
docker build -t my-python-app .

# run the container
docker run my-python-app
```

**output:** `hello from python container!`

---

### bonus — multi-stage dockerfile (smaller image)

```dockerfile
from python:3.11-alpine as builder
workdir /app
copy app.py .

from python:3.11-alpine
workdir /app
copy --from=builder /app/app.py .
cmd ["python", "app.py"]
```

```bash
# build smaller image
docker build -t my-python-app-small .

# compare sizes
docker images | grep my-python-app
```

**base image size:** `20.4mb`

> multi-stage builds shine more with compiled languages like java — where you need a full jdk to build but only a jre to run, significantly reducing the final image size.
