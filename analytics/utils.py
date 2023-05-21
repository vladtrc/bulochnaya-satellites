# предполагается, что все интервалы лежат внутри [a,b]
from dataclasses import dataclass

from typing import List


# дополнение ко множеству
# array: список интервалов
# a, b: границы слева и справа всех интервалов
# получаем интервалы которые не пересекаются с array на участке a,b
def intervals_complement(array, a, b) -> List[List[int]]:
    if len(array) == 0:
        return [[a, b]]
    output_array = [[a, array[0][0]]]
    for i in range(len(array) - 1):
        output_array.append([array[i][1], array[i + 1][0]])
    output_array.append([array[-1][1], b])
    return output_array

# интервалы которые лежат в обоих списках интервалов
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

# суммирование длины по всем интервалам в массиве
def sum_over_intervals(array) -> int:
    return sum([x[1] - x[0] for x in array])


@dataclass
class Visibility:
    start: int
    end: int
    base: str

# обрезаем интервалы до границ a b
def intervals_cut(visibilities: List[Visibility], a, b) -> List[Visibility]:
    res = []
    for visibility in visibilities:
        cut_start = max(a, visibility.start)
        cut_end = min(b, visibility.end)
        if cut_start < cut_end:
            res.append(Visibility(cut_start, cut_end, visibility.base))
    return res


# ограничиваем справа множество интервалов так, чтобы их сумма дала s
def intervals_cut_by_sum(array, s) -> List[List[int]]:
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

# x лежит в одном из интервалов массива
def is_inside_intervals(array, x):
    output = False
    for interval in array:
        if interval[0] <= x <= interval[1]:
            output = True
    return output
