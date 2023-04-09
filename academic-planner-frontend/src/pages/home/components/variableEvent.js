import React, { useEffect } from 'react';
import { TimePicker, Modal, Form, Input, Row, Col, DatePicker } from 'antd';

const VariableEventModal = (props) => {
  const [form] = Form.useForm();

  useEffect(() => {
    !props.isModalOpen && form.resetFields();
    // eslint-disable-next-line
  }, [props]);

  const onSubmit = () => {
    form
      .validateFields()
      .then((values) => {})
      .catch((err) => {});
  };

  return props.isModalOpen ? (
    <Modal
      title={props.isAdd ? 'Add New Schedule' : 'Edit Existing Schedule'}
      open={props.isModalOpen}
      onOk={onSubmit}
      okText={'Save'}
      onCancel={props.handleCancel}
    >
      <Form form={form} initialValues={props.data}>
        <Form.Item
          name={'name'}
          label='Name'
          rules={[{ required: 'true', message: 'Enter Schedule Name!' }]}
        >
          <Input placeholder='Enter Name' />
        </Form.Item>
        <Form.Item
          name='eventDate'
          label='Date'
          hasFeedback
          rules={[{ required: true, message: 'Please select a day!' }]}
        >
          <DatePicker />
        </Form.Item>
        <Row>
          <Col span={12}>
            <Form.Item
              label='Start Time'
              name={'startTime'}
              rules={[
                {
                  type: 'object',
                  required: 'true',
                  message: 'Select Start Time!',
                },
              ]}
            >
              <TimePicker format={'HH:mm'} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              label='End Time'
              name={'endTime'}
              rules={[
                {
                  type: 'object',
                  required: 'true',
                  message: 'Select End Time!',
                },
              ]}
            >
              <TimePicker format={'HH:mm'} />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  ) : (
    <></>
  );
};

export default VariableEventModal;
