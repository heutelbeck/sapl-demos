function handleGeoJsonData(event, layerGroup, geoJsonKey, tooltipKey, color) {
            var geoJsonArray = JSON.parse(event.data);
    layerGroup.clearLayers();

    geoJsonArray.forEach(function(geoJson) {
        L.geoJSON(geoJson[geoJsonKey], {
            style: function (feature) {
                return {
                    color: color,
                    weight: 2,
                    fill: false
                };
            },
            onEachFeature: function (feature, layer) {
                if (geoJson[tooltipKey]) {
                    layer.bindTooltip(geoJson[tooltipKey]);
                }
            }
        }).addTo(layerGroup);
    });
}

function handleGeoJsonPosition(event, layerGroup, color) {
    var geoJson = JSON.parse(event.data);
    layerGroup.clearLayers();
    L.geoJSON(geoJson.position, {
        pointToLayer: function (feature, latlng) {
            return L.circleMarker(latlng, {
                radius: 4,
                fillColor: color,
                color: "#000",
                weight: 1,
                opacity: 1,
                fillOpacity: 0.8
            });
        },
        onEachFeature: function (feature, layer) {
            layer.bindPopup("<b>Device ID:</b> " + geoJson.deviceId +
                            "<br><b>Altitude:</b> " + geoJson.altitude + " m" +
                            "<br><b>Last Update:</b> " + geoJson.lastUpdate +
                            "<br><b>Accuracy:</b> " + geoJson.accuracy + " m");
        }
    }).addTo(layerGroup);
}

