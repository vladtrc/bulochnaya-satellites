export const fetcher = (url: string) => {
  //return fetch(process.env.REACT_APP_API_URL + url).then((r) => r.json());
  return fetch("/alex.json").then((r) => r.json());
};
