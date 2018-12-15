from flask import Flask, jsonify, request

app = Flask(__name__)


@app.route('/', methods=["GET", "POST"])
def index():
    if request.method == "GET":
        print(request.headers)
        data = {"code": 10000}
        return jsonify(data)
    elif request.method == "POST":
        print(request.get_json())
        data = {"code": 10000}
        return jsonify(data)

@app.route('/<cluster>/<queue>')
def queue(cluster, queue):
    return jsonify({"code": 10000, "data": ["wufeiqun"]})

if __name__ == '__main__':
    app.debug = False
    app.run(host="0.0.0.0", port=8888)
