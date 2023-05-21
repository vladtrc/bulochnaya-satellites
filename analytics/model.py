from dataclasses import dataclass

from typing import Dict, List


@dataclass
class SatelliteBasePair:
    satellite: str
    base: str


@dataclass
class DurationEntry:  # область видимости
    start: int
    end: int


@dataclass
class DurationDataset:  # одна табличка ихсходного файла
    satelliteBasePair: SatelliteBasePair  # имя базы и спутника
    entries: List[DurationEntry]  # когда база может принимать данные спутника


@dataclass
class Given:
    # Facility2Constellation
    availabilityByBase: Dict[str, List[DurationDataset]]  # Novosib -> []
    availabilityBySatellite: Dict[str, List[DurationDataset]]

    # Russia2Constellation
    availabilityRussia: Dict[str, List[DurationDataset]]

    # сюда все доп константы
    tx_speed: int = 250  # Мегабит/сек отправка на Землю
    rx_speed: int = 4000  # Мегабит/сек фотографирование
    memory_limit: int = 8e6  # Мегабит (1 Терабайт)


# алгоритм


@dataclass
class Result:
    datasets: List[DurationDataset]
