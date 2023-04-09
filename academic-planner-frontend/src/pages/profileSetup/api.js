import axios from '../../axiosConfig';

export const addEvent = async (data) => {
  return await axios.post('/event/fixed', data);
};
