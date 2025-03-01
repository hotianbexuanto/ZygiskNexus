#[derive(Debug)]
pub enum RootImpl {
    Magisk,
    KernelSU,
    None,
}

static mut ROOT_IMPL: RootImpl = RootImpl::None;

pub fn get_root_impl() -> &'static RootImpl {
    // 使用 std::ptr::addr_of! 宏来安全地获取静态引用
    unsafe { std::ptr::addr_of!(ROOT_IMPL).as_ref().unwrap() }
}

pub fn set_root_impl(impl_: RootImpl) {
    unsafe {
        ROOT_IMPL = impl_;
    }
}