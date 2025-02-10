async function fetchJsonData(sha) {
    const url = `https://raw.githubusercontent.com/Quaddicted/quaddicted-data/refs/heads/main/json/by-sha256/${sha.slice(0,2)}/${sha}.json`;

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Failed to fetch data: ${response.status} ${response.statusText}`);
        }
        return await response.json();
    } catch (error) {
        console.error("Error fetching JSON data:", error);
        return null;
    }
}

function buildResultString(json) {
    const tag = json.tags.find(tag => tag.includes("zipbasedir"));
    let type;
    let gamedir;
    if (tag) {
         type = tag.includes("maps") ? "map" : "gamedir";
         gamedir = tag.replace("zipbasedir=", "").replace("/maps/", "").replace("/", "");
    } else {
        console.log("No zipbasedir tag found.");
        type = "mod-folder"
        gamedir = Object.keys(json.files)[0].split("/")[0]
    }
    if (!type || !gamedir) throw new Error("uh oh, what do")
    return `q1package:${json.urls[0]},${type},${gamedir}`;
}

const segments = new URL(location.href).pathname.split("/");
const last = segments.pop() || segments.pop();
const json = await fetchJsonData(last);
console.log(json)
const result = buildResultString(json);
function changeChoice(value) {
    document.querySelector("#one-click").href = result + "," + value;
}
let mapChoices = [];
let startmapTags = json.tags.filter(it => it.includes("startmap"))
if (startmapTags.length > 0){
    startmapTags.map(it => it.replace("startmap=", "")).forEach(it => mapChoices.push(it));
} else {
   mapChoices.push("start")
}
function createElement(){
    var div = document.createElement("tr")
   div.innerHTML = `<td><select ${mapChoices.length > 1 ? "" : "readonly"} onchange="changeChoice(this.value)">${mapChoices.map(choice => `<option value="${choice}">${choice}</option>`)}</select></td><td><a id="one-click" href="${result + "," + mapChoices[0]}">One-click Launch</a></td>`
    return div
}
document.querySelector("#infos tbody").prepend(createElement())
if (result) console.log(result);