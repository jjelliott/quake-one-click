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
    if (!tag) {
        console.log("No zipbasedir tag found.");
        return null;
    }

    const type = tag.includes("maps") ? "map" : "mod";
    const gamedir = tag.replace("zipbasedir=", "").replace("/maps/", "");

    return `q1package:${json.urls[0]},${type},${gamedir}`;
}
const json = await fetchJsonData("d8959d3f3d3dd3043b0a784884549213e8dbe3f7626a586b21964883eb3b1581");

const result = buildResultString(json);
if (result) console.log(result);