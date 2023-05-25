export interface ISatelliteData {
  satelliteName: string;
  start: string;
  end: string;
}

export interface ISatelliteResponse {
  results: { base: string; usage: ISatelliteData[] }[];
  start: string;
  end: string;
}
