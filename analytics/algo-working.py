#!/usr/bin/python3
# параметры алгоритма:
# 1) горизонт планирования. Это квант времени, на которое мы составляем расписание. Пока что пусть оно будет равным половине оборота (3000 секунд).
# 2) общий интервал моделирования. В данном примере - сутки, 86400 секунд.

# входные данные:
# 1) массив параметров каждого спутника в начальный момент времени. Параметры каждого спутника имеют вид следующего списка (структуры):
#    [состояние, количество данных в ЗУ, индикатор сеанса, макс. объем ЗУ, скорость заполнения памяти, скорость сброса]
#
#    может быть 2 варианта состояния:
#    0. Зондирует в данный момент или собирается зондировать на текущем витке;
#    1. Передает или собирается передавать на текущем витке.
#
#    Переключение между состояниями происходит в момент вылета за границы РФ.
#
#    количество данных в ЗУ выражается в гигабитах (и вообще любая память - в гигабитах)
#
#    индикатор сеанса: индекс станции, с которой в данный момент установлен сеанс (если ни с какой, то индикатор равен -1)
#    индикатор сеанса нужен на тот случай, если сеанс придется на границу двух соседних горизонтов планирования
#
#    прочие параметры получены из входных данных
#
# 2) массив статусов станций в начальный момент времени. Статус каждой станции имеет вид следующего списка (структуры):
#    [индикатор сеанса]
#
#    индикатор сеанса: индекс спутника, с которым на данный момент установлен сеанс (если ни с каким, то индикатор равен -1)
#
# 3) массив времен пролета над РФ для каждого спутника (получен напрямую из файла), каждый интервал имеет вид [начало, сек ; конец, сек]
# 4) массив областей видимости для каждого спутника. Каждая область видимости имеет вид : [начало, сек; конец, сек; индекс станции].
#    области видимости отсортированы по моментам начала.

import random

#предполагается, что все интервалы лежат внутри [a,b]
def intervals_complement(array, a, b):
    if len(array) == 0:
        return [[a,b]]
    output_array = [[a, array[0][0]]]
    for i in range(len(array) - 1):
        output_array.append([array[i][1], array[i+1][0]])
    output_array.append([array[-1][1], b])
    return output_array

def intersection(array1, array2):
    i, j, n, m = 0, 0, len(array1), len(array2)
    intersections = []
    while i < n and j < m:
        a, b = max(array1[i][0], array2[j][0]), min(array1[i][1], array2[j][1])
        if a < b:
            intersections.append([a, b])
        if array1[i][1] < array2[j][1]:
            i += 1
        else:
            j += 1
    return intersections

def sum_over_intervals(array):
    return sum([x[1]-x[0] for x in array])

def intervals_cut(array, a, b):
    output_array = []
    for interval in array:
        candidate = [max(a, interval[0]), min(b, interval[1])]

        if len(interval) == 3:
            candidate.append(interval[2])

        if candidate[0] < candidate[1]:
            output_array.append(candidate)
    return output_array

def intervals_cut_by_sum(array, s):
    output_array = []
    rolling_sum = 0
    for interval in array:
        rolling_sum += interval[1] - interval[0]
        if rolling_sum <= s:
            output_array.append(interval)
        else:
            right_bound = interval[1] - rolling_sum + s
            output_array.append([interval[0], right_bound])
            break
    return output_array

def is_inside_intervals(array, x):
    output = False
    for interval in array:
        if interval[0] <= x <= interval[1]:
            output = True
    return output

#хардкодим входные данные - для примера. Потом более красиво тут все сделаю
num_satellites = 1000
num_stations = 30
sat_types = [[8196, 4, 1], [4098, 4, 0.25]]
T_horizon = 3000
T_modeling = 86400

#генерация случайных входных данных (времена пролета, области видимости)
random.seed(2)
satellites = []
flying_over_mother_Russia = []
visibility = []
for i in range(num_satellites):
    sat_type = random.choice(sat_types)
    satellites.append([random.choice([0,1])] + [random.randint(0, sat_type[0])] + sat_type)
    fomR_per_sat = []
    visibility_per_sat = []
    for j in range(86400//6000 + 1):
        interval_start = j*6000 + random.randint(0,5000)
        interval_end = interval_start + random.randint(300,700)
        fomR_per_sat.append([interval_start, interval_end])
        for k in range(num_stations):
            prob = random.random()
            if prob > 0.9:
                vis_start = interval_start + random.randint(-50, 300)
                vis_end = vis_start + random.randint(100,600)
                visibility_per_sat.append([vis_start, vis_end, k])
    visibility.append(visibility_per_sat)
    flying_over_mother_Russia.append(fomR_per_sat)

# сюда будем писать все сеансы связи
output_schedule = []

# далее будут переменные, задающие "индивидуальное расписание" для спутников и станций, т.е. окна, когда станции
# или спутники с кем-то ведут сеанс. В начальный момент времени все спутники и станции свободны, т.е. никакие сеансы не запланированы

stations_free = []
satellites_free = []
for i in range(num_stations):
    stations_free.append([[0, T_modeling]])

for i in range(num_satellites):
    satellites_free.append([[0, T_modeling]])

t_current = 0
total_data_received = 0
total_data_lost = 0
while t_current < T_modeling:
    print(satellites[29])
    for s in range(len(satellites)):
        # сначала обработаем зондирующие спутники - их количество данных увеличится
        # пропорционально суммарному времени, проведенному над РФ. Солнечное время пока не учитываем
        if satellites[s][0] == 0:
            fomR_cut = intersection(flying_over_mother_Russia[s], [[t_current, min(t_current + T_horizon, T_modeling)]])
            gained_data = sum_over_intervals(fomR_cut)*satellites[s][3]
            new_amount_of_data = min(satellites[s][2], satellites[s][1] + gained_data)
            total_data_lost += satellites[s][1] + gained_data - new_amount_of_data
            satellites[s][1] = new_amount_of_data

        # теперь обработаем передающие спутники. В текущей реализации (пока примитивной)
        # берем спутники в произвольном порядке и произвольным образом их "обслуживаем" станциями

        else:
            visibility_cut = intervals_cut(visibility[s], t_current, min(t_current + T_horizon, T_modeling))
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

        # далее описываем все смены состояний спутников (если они происходят в течение текущего T_horizon)
        Russia_departures = [x[1] for x in flying_over_mother_Russia[s]]
        for x in Russia_departures:
            if t_current <= x < t_current + T_horizon:
                satellites[s][0] = 1 - satellites[s][0]

    t_current += min(T_horizon, T_modeling - t_current)


print(total_data_lost, total_data_received)















