import axios from '../../axiosConfig';

export const fetchEvents = async (startDate, endDate) => {
  return await axios.get(`/event?firstDate=${startDate}&secondDate=${endDate}`);
};
