package com.elnfach.realms.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.elnfach.realms.content.Altitude
import com.elnfach.realms.content.Biome
import com.elnfach.realms.content.Humidity
import com.elnfach.realms.content.Temperature
import com.elnfach.realms.gen.world.WorldGen
import com.elnfach.realms.gen.world.location.Location
import com.elnfach.realms.viewmodel.RealmViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RealmScreen(viewModel: RealmViewModel = koinViewModel()) {
    val biomes by viewModel.biomes.collectAsState()
    RealmContent(biomes)
}

@Composable
fun RealmContent(biomes: List<Biome>?) {
    val worldGen = biomes?.let { WorldGen(biomes = it) }

    // Состояние для хранения сгенерированного мира
    var worldMap by remember { mutableStateOf<Array<Array<Location>>?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    var worldWidth by remember { mutableIntStateOf(500) }
    var worldHeight by remember { mutableIntStateOf(500) }

    var altitude by remember { mutableStateOf(false) }
    var toggle by remember { mutableStateOf(false) }
    var humidity by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Заголовок
        Text(
            text = "Генератор мира",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Кнопка генерации
        Column {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .fillMaxWidth() // Занимаем всю ширину
            ) {
                Button(
                    onClick = {
                        if (worldGen != null) {
                            isLoading = true
                            // Генерация в фоновом потоке
                            scope.launch(Dispatchers.Default) {
                                val generatedMap = worldGen.gen(worldWidth, worldHeight)
                                withContext(Dispatchers.Main) {
                                    worldMap = generatedMap
                                    isLoading = false
                                }
                            }
                        }
                    },
                    enabled = worldGen != null && !isLoading,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Генерация...")
                    } else {
                        Text("Сгенерировать мир")
                    }
                }

                Button(
                    onClick = {
                        worldWidth = 25
                        worldHeight = 25
                    }
                ) {
                    Text("25x25")
                }
                Button(
                    onClick = {
                        worldWidth = 100
                        worldHeight = 100
                    }
                ) {
                    Text("100х100")
                }
                Button(
                    onClick = {
                        worldWidth = 250
                        worldHeight = 250
                    }
                ) {
                    Text("250x250")
                }
                Button(
                    onClick = {
                        worldWidth = 500
                        worldHeight = 500
                    }
                ) {
                    Text("500x500")
                }
            }
            Row(Modifier.fillMaxWidth()) {
                Button(onClick =
                    {
                        altitude = !altitude
                    }) {
                    Text("Altitude")
                }
                Button(onClick =
                    {
                        toggle = !toggle
                    }) {
                    Text("Toggle")
                }
                Button(onClick =
                    {
                        humidity = !humidity
                    }) {
                    Text("Humidity")
                }
            }
        }

        // Отображение результата
        when {
            biomes == null -> {
                Text(
                    "Биомы не загружены",
                    color = MaterialTheme.colorScheme.error
                )
            }
            worldMap == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color.LightGray.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Нажмите 'Сгенерировать мир' для создания карты",
                        color = Color.Gray
                    )
                }
            }
            else -> {
                // Визуализация карты мира
                WorldMapVisualization(
                    altitude,
                    toggle,
                    humidity,
                    worldMap = worldMap!!,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                )

                // Статистика
                BiomeLegend(worldMap = worldMap!!)
            }
        }
    }
}

fun getAltitudeColor(biome: Biome): Color {
    val altitude = biome.conditions.altitude.firstOrNull()

    return when (altitude) {
        Altitude.DEEP_OCEAN -> Color(0xFF0D47A1)        // Темно-синий для глубокого океана
        Altitude.OCEAN -> Color(0xFF1976D2)             // Синий для океана
        Altitude.COAST -> Color.Red           // Голубой для побережья
        Altitude.BEACH -> Color(0xFFFFD54F)             // Песочный желтый для пляжа
        Altitude.PLAINS -> Color(0xFF66BB6A)            // Светло-зеленый для равнин
        Altitude.FOREST -> Color(0xFF2E7D32)            // Темно-зеленый для леса
        Altitude.HILLS -> Color(0xFF795548)             // Коричневый для холмов
        Altitude.MOUNTAIN_BASE -> Color(0xFF8D6E63)     // Светло-коричневый для подножия гор
        Altitude.MOUNTAIN -> Color(0xFF5D4037)          // Темно-коричневый для гор
        Altitude.SNOW_PEAKS -> Color(0xFFFFFFFF)        // Белый для заснеженных вершин
        else -> Color(0xFF9E9E9E)                       // Серый по умолчанию
    }
}

fun getTemperatureColor(biome: Biome): Color {
    // Если нужно брать первый элемент из сета температуры
    val temperature = biome.conditions.temperature.firstOrNull()

    return when (temperature) {
        Temperature.FREEZING -> Color.Blue  // Голубой для замерзающих
        Temperature.COLD -> Color(0xFF90CAF9)      // Светло-голубой для холодных
        Temperature.TEMPERATE -> Color(0xFF4CAF50) // Зеленый для умеренных
        Temperature.WARM -> Color(0xFFFFA726)      // Оранжевый для теплых
        Temperature.HOT -> Color(0xFFF44336)       // Красный для горячих
        else -> Color(0xFF9E9E9E) // Серый по умолчанию
    }
}


fun getHumidityColor(biome: Biome): Color {
    // Если нужно брать первый элемент из сета влажности
    val humidity = biome.conditions.humidity.firstOrNull()

    return when (humidity) {
        Humidity.ARID -> Color(0xFFFFA726) // Оранжевый для засушливых
        Humidity.DRY -> Color(0xFFFFCA28)  // Желто-оранжевый для сухих
        Humidity.MOIST -> Color(0xFF4CAF50) // Зеленый для влажных
        Humidity.WET -> Color(0xFF2196F3)  // Синий для мокрых
        else -> Color(0xFF9E9E9E) // Серый по умолчанию
    }
}
fun getBiomeColor2(biome: Biome): Color {
    return when {
        biome.conditions.altitude.contains(Altitude.DEEP_OCEAN) -> Color(0xFF000080) // Темно-синий
        biome.conditions.altitude.contains(Altitude.OCEAN) -> Color(0xFF0000FF) // Синий
        biome.conditions.altitude.contains(Altitude.COAST) -> Color(0xFFFFFF00) // Желтый
        biome.conditions.altitude.contains(Altitude.BEACH) -> Color(0xFFF4E842) // Песочный
        biome.conditions.altitude.contains(Altitude.PLAINS) -> Color(0xFF90EE90) // Светло-зеленый
        biome.conditions.altitude.contains(Altitude.FOREST) -> Color(0xFF228B22) // Темно-зеленый
        biome.conditions.altitude.contains(Altitude.HILLS) -> Color(0xFFA9A9A9) // Серый
        biome.conditions.altitude.contains(Altitude.MOUNTAIN_BASE) -> Color(0xFF696969) // Темно-серый
        biome.conditions.altitude.contains(Altitude.MOUNTAIN) -> Color(0xFF404040) // Очень темно-серый
        biome.conditions.altitude.contains(Altitude.SNOW_PEAKS) -> Color(0xFFFFFFFF) // Белый
        else -> Color.Black // fallback
    }
}

fun getBiomeColor(biome: Biome): Color {
    return when (biome.family.type) {
        "open_ocean" -> when (biome.id) {
            "tropical_ocean" -> Color(0xFF00BCD4) // Бирюзово-голубой для тропиков
            "temperate_ocean" -> Color(0xFF2196F3) // Яркий синий для умеренных вод
            "subpolar_ocean" -> Color(0xFF1976D2) // Темно-синий для холодных вод
            "arctic_ocean" -> Color(0xFF1565C0) // Очень темный синий для арктики
            else -> Color(0xFF1976D2)
        }

        "deep_ocean" -> when (biome.id) {
            "deep_trench" -> Color(0xFF0D47A1) // Глубокий синий для желобов
            "oceanic_plateau" -> Color(0xFF1E88E5) // Синий для плато
            "deep_current" -> Color(0xFF42A5F5) // Голубоватый для течений
            else -> Color(0xFF1565C0)
        }

        "biological_zones" -> when (biome.id) {
            "coral_sea" -> Color(0xFF00BFA5) // Бирюзово-зеленый для кораллов
            "kelp_forest_water" -> Color(0xFF388E3C) // Зеленый для водорослей
            "sargasso_sea" -> Color(0xFF4CAF50) // Светло-зеленый для саргассума
            "phytoplankton_bloom" -> Color(0xFF66BB6A) // Ярко-зеленый для планктона
            else -> Color(0xFF4CAF50)
        }

        "unique_marine" -> when (biome.id) {
            "deep_thermal_vents" -> Color(0xFFFF5722) // Оранжево-красный для гидротерм
            "cold_seep" -> Color(0xFF795548) // Коричневый для метановых сипов
            "underwater_caves" -> Color(0xFF607D8B) // Серо-голубой для пещер
            else -> Color(0xFF9E9E9E)
        }

        "seafloor" -> when (biome.id) {
            "abyssal_plain" -> Color(0xFF0D47A1) // Темнейший синий для равнин
            "seamount" -> Color(0xFF1E88E5) // Синий для подводных гор
            "oceanic_ridge" -> Color(0xFF2196F3) // Яркий синий для хребтов
            else -> Color(0xFF1565C0)
        }

        "deepest_ocean" -> when (biome.id) {
            "hadal_zone" -> Color(0xFF0A2A5C) // Почти черный синий для глубочайших вод
            else -> Color(0xFF0D47A1)
        }

        // Хвойные леса - разные оттенки зеленого
        "CONIFEROUS" -> when (biome.id) {
            "deep_taiga" -> Color(0xFF2D5A27) // Темно-зеленый
            "pine_forest" -> Color(0xFF3D7141) // Сосновый зеленый
            "cedar_forest" -> Color(0xFF4A7C59) // Кедровый
            else -> Color(0xFF2E7D32)
        }

        // Лиственные леса - светлые зеленые
        "LEAFY" -> when (biome.id) {
            "mirkwood_forest" -> Color(0xFF1B5E20) // Темный дубовый
            "birch_forest" -> Color(0xFF81C784) // Светлый березовый
            "beech_forest" -> Color(0xFF4CAF50) // Буковый
            "swamp_forest" -> Color(0xFF388E3C) // Болотный зеленый
            "aspen_forest" -> Color(0xFF66BB6A) // Осиновый
            else -> Color(0xFF43A047)
        }

        // Смешанные леса
        "MIXED" -> when (biome.id) {
            "mixed_forest" -> Color(0xFF558B2F)
            "linden_forest" -> Color(0xFF689F38)
            "urema" -> Color(0xFF7CB342)
            "mountain_forest" -> Color(0xFF33691E)
            "edge_forest" -> Color(0xFF9CCC65)
            "coastal_pine_forest" -> Color(0xFFAED581)
            "juniper_woodlands" -> Color(0xFFC5E1A5)
            "swampy_spruce_forest" -> Color(0xFF5D4037)
            "floodplain_poplar_forest" -> Color(0xFF8BC34A)
            else -> Color(0xFF689F38)
        }

        // Равнины и степи
        "plains" -> when (biome.id) {
            "temperate_plains" -> Color(0xFFC8E6C9) // Светло-зеленый
            "arid_steppe" -> Color(0xFFD7CCC8) // Бежевый
            "floodplain_meadow" -> Color(0xFFA5D6A7) // Зеленый луг
            "cold_plain" -> Color(0xFFBBDEFB) // Холодный голубоватый
            else -> Color(0xFFC8E6C9)
        }

        // Луга
        "meadows" -> when (biome.id) {
            "alpine_meadow" -> Color(0xFFE8F5E8) // Очень светлый зеленый
            "water_meadow" -> Color(0xFFA5D6A7) // Влажный зеленый
            "dry_meadow" -> Color(0xFFC5E1A5) // Сухой зеленый
            else -> Color(0xFFDCEDC8)
        }

        // Холмы
        "hills" -> when (biome.id) {
            "rolling_hills" -> Color(0xFF8BC34A) // Зеленые холмы
            "badlands" -> Color(0xFFBCAAA4) // Бежево-коричневый
            "chalk_hills" -> Color(0xFFFAFAFA) // Белый
            else -> Color(0xFF9E9E9E)
        }

        // Болота
        "wetlands" -> when (biome.id) {
            "marsh" -> Color(0xFF4DB6AC) // Болотный зеленый
            "bog" -> Color(0xFF00695C) // Темный болотный
            "fen" -> Color(0xFF00796B) // Тростниковый
            else -> Color(0xFF004D40)
        }

        // Пустоши
        "heathlands" -> Color(0xFFBA68C8) // Фиолетовый для вереска

        // Прибрежные зоны
        "coastal" -> when (biome.id) {
            "sand_dunes" -> Color(0xFFFFF176) // Желтый песок
            "sandy_beach" -> Color(0xFFFFF59D) // Светлый песок
            "rocky_shore" -> Color(0xFF9E9E9E) // Серый
            "coastal_cliffs" -> Color(0xFF757575) // Темно-серый
            "coastal_dunes" -> Color(0xFFFFEB3B) // Яркий песок
            "tidal_flats" -> Color(0xFFFFF8E1) // Светло-песочный
            "sea_cliffs" -> Color(0xFF616161) // Темно-серый для морских утесов
            else -> Color(0xFFFFF176)
        }

        // Горы
        "mountains" -> when (biome.id) {
            "alpine_tundra" -> Color(0xFFE0E0E0) // Светло-серый
            "rocky_peaks" -> Color(0xFF9E9E9E) // Серый
            "mountain_meadow" -> Color(0xFFC8E6C9) // Горный луг
            "high_desert" -> Color(0xFFD7CCC8) // Горная пустыня
            "volcanic_slopes" -> Color(0xFF5D4037) // Коричневый
            "mountain_pass" -> Color(0xFFBDBDBD) // Перевал
            else -> Color(0xFF9E9E9E)
        }

        // Горные леса
        "mountain_forests" -> when (biome.id) {
            "mountain_taiga" -> Color(0xFF33691E) // Темный хвойный
            "montane_forest" -> Color(0xFF558B2F) // Горный лес
            else -> Color(0xFF2E7D32)
        }

        // Горные долины
        "mountain_valleys" -> Color(0xFFAED581) // Светлый зеленый

        // Ледниковые зоны
        "glacial" -> when (biome.id) {
            "glacial_valley" -> Color(0xFFE3F2FD) // Ледниково-голубой
            "permanent_snow" -> Color(0xFFFFFFFF) // Белый снег
            else -> Color(0xFFB3E5FC)
        }

        // Крутые склоны
        "steep_slopes" -> when (biome.id) {
            "scree_slope" -> Color(0xFF795548) // Коричневый
            "cliff_face" -> Color(0xFF607D8B) // Серо-голубой
            else -> Color(0xFF9E9E9E)
        }

        // Предгорья
        "foothills" -> Color(0xFF8BC34A) // Зеленый

        // Лагуны
        "lagoons" -> Color(0xFF4FC3F7) // Светло-голубой

        // Заливы
        "bays" -> Color(0xFF29B6F6) // Голубой

        // Открытые воды
        "open_water" -> when (biome.id) {
            "open_sea" -> Color(0xFF1976D2) // Темно-синий
            "coastal_waters" -> Color(0xFF2196F3) // Синий
            else -> Color(0xFF1976D2)
        }

        // Озера
        "lakes" -> when (biome.id) {
            "freshwater_lake" -> Color(0xFF03A9F4) // Голубой
            "mountain_lake" -> Color(0xFF4FC3F7) // Светло-голубой
            else -> Color(0xFF03A9F4)
        }

        // Реки
        "rivers" -> when (biome.id) {
            "major_river" -> Color(0xFF0288D1) // Речной синий
            "stream" -> Color(0xFF4FC3F7) // Ручей
            else -> Color(0xFF0288D1)
        }

        // Особые водные зоны
        "special_water" -> when (biome.id) {
            "hot_springs" -> Color(0xFFFF7043) // Оранжевый для горячих источников
            "frozen_waters" -> Color(0xFFE1F5FE) // Ледяной голубой
            else -> Color(0xFF0288D1)
        }

        // Тропические зоны
        "TROPICAL" -> when (biome.id) {
            "tropical_rainforest" -> Color(0xFF1B5E20) // Темно-зеленый для джунглей
            "monsoon_forest" -> Color(0xFF388E3C) // Зеленый для муссонного леса
            "mangrove_swamp" -> Color(0xFF00695C) // Темно-болотный для мангров
            "tropical_savanna" -> Color(0xFFC8E6C9) // Светло-зеленый для саванны
            else -> Color(0xFF4CAF50)
        }

        // Пустыни
        "DESERT" -> when (biome.id) {
            "sand_desert" -> Color(0xFFFFF176) // Песочный
            "rock_desert" -> Color(0xFF9E9E9E) // Серый для каменной пустыни
            "salt_flats" -> Color(0xFFFFFFF8) // Белый для солончаков
            "oasis" -> Color(0xFFA5D6A7) // Зеленый для оазиса
            else -> Color(0xFFFFF59D)
        }

        // Полярные зоны
        "POLAR" -> when (biome.id) {
            "arctic_tundra" -> Color(0xFFE0E0E0) // Светло-серый
            "ice_desert" -> Color(0xFFFFFFFF) // Белый для ледяной пустыни
            "permafrost" -> Color(0xFFB3E5FC) // Голубоватый для мерзлоты
            else -> Color(0xFFE3F2FD)
        }

        // Уникальные ландшафты
        "UNIQUE" -> when (biome.id) {
            "canyon" -> Color(0xFF795548) // Коричневый для каньона
            "mesa" -> Color(0xFF8D6E63) // Светло-коричневый для месы
            "thermal_springs" -> Color(0xFFFF7043) // Оранжевый для гейзеров
            "karst_landscape" -> Color(0xFFBCAAA4) // Бежевый для карста
            else -> Color(0xFF9E9E9E)
        }

        // Запасной вариант
        else -> {
            // Генерируем цвет на основе хеша имени биома
            val hash = biome.name.first().hashCode()
            Color(
                red = (hash % 100 + 100) / 255f,
                green = (hash % 155 + 100) / 255f,
                blue = (hash % 200 + 55) / 255f
            )
        }
    }
}
@Composable
fun WorldMapVisualization(
    altitude: Boolean,
    toggle: Boolean,
    humidity: Boolean,
    worldMap: Array<Array<Location>>,
    modifier: Modifier = Modifier
) {
    val height = worldMap.size
    val width = worldMap[0].size

    Canvas(modifier = modifier) {
        val cellWidth = size.width / width
        val cellHeight = size.height / height

        for (y in 0 until height) {
            for (x in 0 until width) {
                val location = worldMap[y][x]
                val color = if (!altitude) getAltitudeColor(location.biome) else if (!humidity) if (toggle) getBiomeColor(location.biome) else getTemperatureColor(location.biome) else getHumidityColor(location.biome)

                drawRect(
                    color = color,
                    topLeft = Offset(x * cellWidth, y * cellHeight),
                    size = androidx.compose.ui.geometry.Size(cellWidth, cellHeight)
                )
            }
        }
    }
}
@Composable
fun BiomeLegend(worldMap: Array<Array<Location>>) {
    val uniqueBiomes = remember(worldMap) {
        worldMap.flatMap { it.toList() }
            .map { it.biome }
            .distinctBy { it.id }
            .sortedBy { it.family.type }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Легенда биомов",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn {
                items(uniqueBiomes) { biome ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(getBiomeColor(biome))
                                .border(1.dp, Color.Black)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = biome.name.first(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "(${biome.family.type})",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun RealmScreenPreview()
{
    //RealmContent()
}