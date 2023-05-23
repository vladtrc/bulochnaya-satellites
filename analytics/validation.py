from tqdm import tqdm
from analytics.functions import generate_randoms, calculate_schedule


def validate_schedule(output_schedule, visibility, verbose = False):
    # Initialize a dictionary to hold all the scheduled transmissions for each station
    scheduled_transmissions = {}

    # Initialize a dictionary to hold all the scheduled transmissions for each satellite
    scheduled_sat_transmissions = {}

    for idx, schedule in tqdm(enumerate(output_schedule), total=len(output_schedule)):
        start, end, satellite, station, data_amount = schedule
        print(f"\33[095mChecking transmission {idx}...\33[0m") if verbose else None

        # Check if the transmission time is within the visibility interval of the satellite
        visibility_sat = visibility[satellite]  # Visibility of satellite: [[start, end, station], ...]
        print("\33[090mChecking visibility...\33[0m", end=" ") if verbose else None
        is_in_visibility = any([start >= v_start and end <= v_end for v_start, v_end, _ in visibility_sat])
        if not is_in_visibility:
            print(
                f"ERROR: Transmission {idx} was not in visibility interval: ({start}, {end}) for satellite {satellite}")
            break
        print("\33[092mOK\33[0m") if verbose else None

        # Check if there was a transmission to the same station from more than one satellite at the same time
        print("\33[090mChecking overlapping for station...\33[0m", end=" ") if verbose else None
        if station not in scheduled_transmissions:
            scheduled_transmissions[station] = []
        for sat_start, sat_end, sat in scheduled_transmissions[station]:
            if (start >= sat_start and start < sat_end) or (end > sat_start and end <= sat_end):
                if not satellite == sat:
                    print(f"ERROR: There was a transmission to station {station} from satellites {satellite} "
                          f"and {sat} at overlapping time intervals.")
                    break
        else:
            scheduled_transmissions[station].append([start, end, satellite])
        print("\33[092mOK\33[0m") if verbose else None

        # Check if there was a transmission from one satellite to more than one station at the same time
        print("\33[090mChecking overlapping for satellite...\33[0m", end=" ") if verbose else None
        if satellite not in scheduled_sat_transmissions:
            scheduled_sat_transmissions[satellite] = []
        for sat_start, sat_end, stat in scheduled_sat_transmissions[satellite]:
            if (start >= sat_start and start < sat_end) or (end > sat_start and end <= sat_end):
                if not station == stat:
                    print(f"ERROR: There was a transmission from satellite {satellite} to stations {station} "
                          f"and {stat} at overlapping time intervals.")
                    break
        else:
            scheduled_sat_transmissions[satellite].append([start, end, station])
        print("\33[092mOK\33[0m") if verbose else None

    # Check if all transmissions were of positive non-zero data amounts
    print("\33[090mChecking data amounts...\33[0m", end=" ") if verbose else None
    for idx, schedule in enumerate(output_schedule):
        _, _, _, _, data_amount = schedule
        if data_amount <= 0:
            print(f"ERROR: Transmission {idx} contains non-positive or zero data amount: {data_amount}.")
            print("\33[096mFAILED\33[0m")
            break
    print("\33[092mOK\33[0m") if verbose else None


def main():
    satellites, flying_over_mother_Russia, visibility = generate_randoms()
    output_schedule = calculate_schedule(satellites, flying_over_mother_Russia, visibility)
    validate_schedule(output_schedule, visibility)


if __name__ == "__main__":
    main()
