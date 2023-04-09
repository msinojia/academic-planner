import axios from '../../axiosConfig';

export const fetchEvents = async (startDate, endDate) => {
  const res = await axios.get(
    `/event?firstDate=${startDate}&secondDate=${endDate}`
  );
  console.log(res);
  return res;
};

export const addVariableEvent = async (data) => {
  return await axios.post('/event/variable', data);
};
