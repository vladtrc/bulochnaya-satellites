from typing import List

from analytics.model import Given, Result, DurationDataset
from analytics.utils import intersection, sum_over_intervals, intervals_cut, Visibility, intervals_complement, \
    intervals_cut_by_sum


@dataclass
class SatelliteState:
    name: str
    memory: int  # заполненная память МБит
    intention: str  # 'scan' 'transmit'

@dataclass
class ScheduleEntry:
    begin: int
    end: int
    satellite: str
    base: str

@dataclass
class Result:
    schedule: List[ScheduleEntry]
    data_lost : int # Мбит
    data_received: int # Мбит

def duration_dataset_to_visibility(duration_dataset: DurationDataset) -> List[Visibility]:
    return [Visibility(e.start, e.end, duration_dataset.satelliteBasePair.base) for e in duration_dataset.entries]

def algo(given: Given) -> Result:
    step = 3000  # шаг
    T_modeling = 86400  # всё время
    t_current = 0
    total_data_lost = 0
    total_data_received = 0
    output_schedule = []

    # мапы, содержащие индивидуальное расписание для баз и спутников. Изначально все базы и спутники свободны, т.е. никакие сеансы не запланированы
    # далее из общего отрезка [0, T_modeling] будут вырезаться интервалы по мере планирования новых сеансов
    bases_free = {name: [[0, T_modeling]] for name in given.availabilityByBase}
    satellites_free = {name: [[0, T_modeling]] for name in given.availabilityBySatellite}

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
                # выше мы получили области видимости на текущем шаге для данного спутника
                # однако если ранее уже были запланированы какие-либо сеансы, может оказаться так, что не все время в течение областей видимости 
                # доступно для планирования новых сеансов
                # поэтому прореживаем области видимости с учетом занятости станций:
                for v in visibility_cut:
                    visibility_with_load_elem = intersection([[v.start, v.end]], stations_free(v.base))
                    visibility_with_load += [Visibility(x[0], x[1], v.base) for x in visibility_with_load_elem]

                # теперь имеем на руках интервалы - кандидаты для приема данных со спутника.
                # эти интервалы соответствуют различным базам, могут пересекаться и не отсортированы 
                # поэтому при планировании сеансов со спутником нужно учитывать занятость самого спутника (мапа satellites_free)
                for v in visibility_with_load:
                    # в первую очередь превратим каждый интервал во множество интервалов с учетом занятости спутника
                    intervals_busy = intersection([[v.start, v.end]], satellites_free(s.name))
                    # об этом думаем как о вспомогательной переменной
                    not_intervals_busy = intervals_complement(intervals_busy, 0, T_modeling)
                    # максимальное количество данных, которое может быть передано на Землю на интервалах intervals_busy
                    max_amount_of_data = given.tx_speed * sum_over_intervals(intervals_busy)

                    # далее два варианта. Либо на спутнике достаточно данных, чтобы запланировать сеансы на все интервалы из intervals_busy
                    if max_amount_of_data < s.memory:
                        # исключаем из индивидуального расписания базы время, на которое мы планируем сеансы
                        stations_free(v.base) = intersection(stations_free(v.base), not_intervals_busy)
                        # исключаем те же самые интервалы из расписания спутника
                        satellites_free(s.name) = intersection(satellites_free(s.name), not_intervals_busy)
                        # добавляем строчки в итоговое расписание сеансов
                        output_schedule += [ScheduleEntry(x[0], x[1], s.name, v.base) for x in intervals_busy]
                        total_data_received += max_amount_of_data
                        s.memory -= max_amount_of_data
                    # либо на спутнике данных недостаточно. Тогда используем все интервалы вплоть до исчерпания данных на спутнике
                    else:
                        #обрезаем интервалы так, чтобы на оставшихся интервалах спутник мог передать все свои данные
                        intervals_busy = intervals_cut_by_sum(intervals_busy, s.memory/s.tx_speed)
                        not_intervals_busy = intervals_complement(intervals_busy, 0, T_modeling)
                        stations_free(v.base) = intersection(stations_free(v.base), not_intervals_busy)
                        satellites_free(s.name) = intersection(satellites_free(s.name), not_intervals_busy)
                        output_schedule += [ScheduleEntry(x[0], x[1], s.name, v.base) for x in intervals_busy]
                        total_data_received += s.memory
                        s.memory = 0
                        break

            # далее описываем все смены состояний спутников (если они происходят в течение текущего step)
            # смена состояния происходит после последнего вылета с территории России на текущем витке
            # так как пролеты над Россией могут быть устроены нетривиально (состоять из нескольких небольших интервалов, например)
            # будем считать, что смены состояния происходят через пол-витка после какого-либо из вылетов за границы России
            state_changing_times = [x[1] + 3000 for x in russia_ranges]
            for x in state_changing_times:
                if t_current <= x < t_current + step:
                    if s.intention == 'scan':
                        s.intention = 'transmit'
                    else:
                        s.intention = 'scan'

        t_current += min(step, T_modeling - t_current)
    result = Result(output_schedule, total_data_lost, total_data_received)
    return result
