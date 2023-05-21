from typing import List

from analytics.model import Given, Result, DurationDataset
from analytics.utils import intersection, sum_over_intervals, intervals_cut, Visibility, intervals_complement, \
    intervals_cut_by_sum


@dataclass
class SatelliteState:
    name: str
    memory: int  # заполненная память МБит
    intention: str  # 'scan' 'transmit'


def duration_dataset_to_visibility(duration_dataset: DurationDataset) -> List[Visibility]:
    return [Visibility(e.start, e.end, duration_dataset.satelliteBasePair.base) for e in duration_dataset.entries]


def algo(given: Given) -> Result:
    step = 3000  # шаг
    T_modeling = 86400  # всё время
    t_current = 0
    total_data_lost = 0
    stations_free = []
    satellites_free = []

    satellites = [SatelliteState(name, 0, 'scan') for name in given.availabilityBySatellite]
    visibilities = {name: duration_dataset_to_visibility(datasets) for name, datasets in given.availabilityBySatellite.items()}

    while t_current < T_modeling:
        for s in satellites:
            russia_ranges = given.availabilityRussia.get(s.name)
            if s.intention == 'scan':

                # интервалы в которые он будет над РФ в течении текущего шага
                fomR_cut = intersection(russia_ranges, [[t_current, min(t_current + step, T_modeling)]])

                # кол-во данных которые мы получили в течении интервалов
                gained_data = sum_over_intervals(fomR_cut) * given.rx_speed

                # кол-во данных на спутнике в конце шага
                new_amount_of_data = min(given.memory_limit, s.memory + gained_data)
                total_data_lost += s.memory + gained_data - new_amount_of_data
                s.memory = new_amount_of_data

            # теперь обработаем передающие спутники. В текущей реализации (пока примитивной)
            # берем спутники в произвольном порядке и произвольным образом их "обслуживаем" станциями

            else:  # intention = 'transmit'
                visibility_cut = intervals_cut(visibilities[s.name], t_current, min(t_current + step, T_modeling))
                visibility_with_load = []
                # прореживаем области видимости с учетом занятости станций:
                for interval in visibility_cut:
                    visibility_with_load_elem = intersection([interval[:2]], stations_free[interval[2]])
                    visibility_with_load += [x + [interval[2]] for x in visibility_with_load_elem]

                # теперь имеем на руках интервалы - кандидаты для приема данных со спутника.
                # на этом этапе можно отсортировать станции приема по некоторому критерию. Но пока этого не делаем
                for interval in visibility_with_load:
                    intervals_busy = intersection([interval[:2]], satellites_free[s])
                    not_intervals_busy = intervals_complement(intervals_busy, 0, T_modeling)
                    max_amount_of_data = satellites[s][3] * sum_over_intervals(intervals_busy)
                    if max_amount_of_data < satellites[s][1]:
                        satellites[s][1] -= max_amount_of_data
                        stations_free[interval[2]] = intersection(stations_free[interval[2]], not_intervals_busy)
                        satellites_free[s] = intersection(satellites_free[s], not_intervals_busy)
                        output_schedule += [x + [s, interval[2], max_amount_of_data] for x in intervals_busy]
                        total_data_received += max_amount_of_data
                    else:
                        intervals_busy = intervals_cut_by_sum(intervals_busy, satellites[s][1])
                        not_intervals_busy = intervals_complement(intervals_busy, 0, T_modeling)
                        stations_free[interval[2]] = intersection(stations_free[interval[2]], not_intervals_busy)
                        satellites_free[s] = intersection(satellites_free[s], not_intervals_busy)
                        output_schedule += [x + [s, interval[2], satellites[s][1]] for x in intervals_busy]
                        total_data_received += satellites[s][1]
                        satellites[s][1] = 0
                        break

        # далее описываем все смены состояний спутников (если они происходят в течение текущего step)
        Russia_departures = [x[1] for x in flying_over_mother_Russia[s]]
        for x in Russia_departures:
            if t_current <= x < t_current + step:
                satellites[s][0] = 1 - satellites[s][0]

    t_current += min(step, T_modeling - t_current)

